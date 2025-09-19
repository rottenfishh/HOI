package ru.nsu.kolodina.keys;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor
public class OutputThread implements Runnable {
    final LinkedBlockingQueue<ClientConnection> requestsQueue;
    final LinkedBlockingQueue<ClientConnection> outputQueue;
    final Selector outputSelector; // selector of output would be needed everywhere
    // стоит селектор на сокеты клиентов
    // ждем на очереди аутпут

    @Override
    public void run() {
        try {
            outputSelector.select(); // ждем пока не будет открытых сокетов на запись или пока нас не разбудят генер нить
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (SelectionKey key : outputSelector.selectedKeys()) {
            if (key.isWritable()) {

            }
        }
    }
}
