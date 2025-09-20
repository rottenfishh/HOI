package ru.nsu.kolodina.keys;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;
// переделать
@RequiredArgsConstructor
public class GeneratingThread  implements Runnable {
    KeyGeneration keyGeneration = new KeyGeneration();
    final Selector outputSelector; // selector of output would be needed everywhere
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
                if (clients.containsKey(c.name)) {
                    KeyState state = clients.get(c.name);
                    if (state.ready.get()) outputQueue.put(c); else state.clients.add(c); // ключ готов - отправим в аутпут. ключа нет - добавить в мапу этого ключа
                } else {
                    KeyState state = clients.computeIfAbsent(c.name, k -> new KeyState());
                    if (!state.generating.getAndSet(true)) {
                        state.clients.add(c);
                        state.key = keyGeneration.generateKeys(c.name);
                        state.ready.set(true); //что если пока мы генерировали, на это имя добавились соединения? лист не потокобезопасен, надо добавить синхронизацию на нем тоже
                        for (ClientConnection cl : state.clients) {
                            cl.rsaKey = state.key;
                            outputQueue.put(cl);
                        }
                    }
                }
                outputSelector.wakeup();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
