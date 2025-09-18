package ru.nsu.kolodina.keys;

import lombok.AllArgsConstructor;

import java.security.KeyPair;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

import static java.lang.Thread.sleep;

@AllArgsConstructor
public class GeneratingThread  implements Runnable {
    Generation generation = new Generation();

    final Deque<ClientConnection> generationRequests;
    final Deque<ClientConnection> generationResults;

    @Override
    public void run() {
        synchronized(generationRequests) {
            if (generationRequests.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                ClientConnection c = generationRequests.pop();
                KeyPair keys = generation.generateKeys();
                c.rsaKey = keys.toString(); // placeholder
                synchronized(generationResults) {
                    generationResults.push(c);
                }
            }
        }
    }
}
