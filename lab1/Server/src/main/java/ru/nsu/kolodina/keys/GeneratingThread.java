package ru.nsu.kolodina.keys;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.channels.Selector;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;

@RequiredArgsConstructor
public class GeneratingThread  implements Runnable {
    KeyGeneration keyGeneration = new KeyGeneration();
    final Selector outputSelector; // selector of output would be needed everywhere
    @NonNull
    final LinkedBlockingQueue<ClientConnection> requestsQueue;
    @NonNull
    final LinkedBlockingQueue<ClientConnection> outputQueue;

    @Override
    public void run() {
        try {
            ClientConnection c = requestsQueue.take();
            c.rsaKey = keyGeneration.generateKeys(c.name);
            outputQueue.put(c);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
