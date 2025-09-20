package ru.nsu.kolodina.keys;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    public void writeToClient(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientConnection c = (ClientConnection) key.attachment();
        ByteBuffer byteBuffer =StandardCharsets.UTF_8.encode(c.rsaKey.toString());
        try {
            clientChannel.write(byteBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                outputSelector.select(); // ждем пока не будет открытых сокетов на запись или пока нас не разбудят генер нить
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (SelectionKey key : outputSelector.selectedKeys()) {
                if (key.isWritable()) {
                    writeToClient(key);
                }
            }
            outputSelector.selectedKeys().clear();
            ClientConnection c;
            while ((c = outputQueue.poll()) != null) {
                SocketChannel clientChannel = c.clientChannel;
                try {
                    clientChannel.register(outputSelector, SelectionKey.OP_WRITE).attach(c);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
