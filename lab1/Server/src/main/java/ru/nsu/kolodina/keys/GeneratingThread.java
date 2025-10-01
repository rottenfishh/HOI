package ru.nsu.kolodina.keys;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.nsu.kolodina.keys.entity.ClientConnection;
import ru.nsu.kolodina.keys.entity.KeyState;
import ru.nsu.kolodina.keys.utils.RsaKeyManagement;

import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import static ru.nsu.kolodina.keys.entity.KeyState.State.*;

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

                KeyState keyState = clients.computeIfAbsent(c.name, k -> new KeyState());
                if (keyState.state.getAndSet(GENERATING).equals(GENERATING)) {
                    synchronized (keyState) {
                        if (keyState.state.get().equals(READY)) outputQueue.put(c);
                        else keyState.keyClients.add(c);
                    }
                } else {
                    keyState.keyClients.add(c);
                    keyState.key = rsaKeyManagement.generateClientKeys(c.name);
                    System.out.println("Generating key for client " + c.name);
                    keyState.state.set(READY);
                    for (ClientConnection cl : keyState.keyClients) {
                        cl.rsaKey = keyState.key;
                        outputQueue.put(cl);
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
