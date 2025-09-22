package ru.nsu.kolodina.keys;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@RequiredArgsConstructor
public class GeneratingThread  implements Runnable {

    RsaKeyManagement rsaKeyManagement = new RsaKeyManagement();
    final Selector outputSelector;
    ConcurrentHashMap<String, KeyState> clients= new ConcurrentHashMap<>();
    @NonNull
    final LinkedBlockingQueue<ClientConnection> requestsQueue;
    @NonNull
    final LinkedBlockingQueue<ClientConnection> outputQueue;

    @Override
    public void run() {
        while (true) {
            try {
                ClientConnection c = requestsQueue.take();
                System.out.println("Client received in generating thread");
                if (clients.containsKey(c.name)) {
                    KeyState state = clients.get(c.name);
                    if (state.ready.get()) outputQueue.put(c); else state.keyClients.add(c); // ключ готов - отправим в аутпут. ключа нет - добавить в мапу этого ключа
                } else {
                    KeyState state = clients.computeIfAbsent(c.name, k -> new KeyState());
                    if (!state.generating.getAndSet(true)) {
                        state.keyClients.add(c);
                        state.key = rsaKeyManagement.generateClientKeys(c.name);
                        state.ready.set(true); //что если пока мы генерировали, на это имя добавились соединения? лист не потокобезопасен, надо добавить синхронизацию на нем тоже
                        for (ClientConnection cl : state.keyClients) {
                            cl.rsaKey = state.key;
                            System.out.println("keys got" + cl.rsaKey.keyPair.toString());
                            outputQueue.put(cl);
                        }
                    }
                }
                System.out.println("output wake up");
                outputSelector.wakeup();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
