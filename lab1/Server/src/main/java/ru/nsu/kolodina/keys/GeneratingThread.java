package ru.nsu.kolodina.keys;

import lombok.AllArgsConstructor;

import java.util.Deque;

import static java.lang.Thread.sleep;

@AllArgsConstructor
public class GeneratingThread  implements Runnable {
    KeyGeneration keyGeneration = new KeyGeneration();

    final Deque<ClientConnection> requestsQueue;
    final Deque<ClientConnection> outputQueue;

    @Override
    public void run() {
        synchronized(requestsQueue) {
            if (requestsQueue.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                ClientConnection c = requestsQueue.pop();
                c.rsaKey = keyGeneration.generateKeys(c.name);
                synchronized(outputQueue) {
                    outputQueue.push(c);
                }
            }
        }
    }
}
