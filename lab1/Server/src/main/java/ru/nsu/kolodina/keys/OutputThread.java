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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor
public class OutputThread implements Runnable {
    final LinkedBlockingQueue<ClientConnection> outputQueue;
    final Selector outputSelector; // selector of output would be needed everywhere
    // стоит селектор на сокеты клиентов
    // ждем на очереди аутпут

    @Override
    public void run() {
        while (true) {
            try {
                outputSelector.select(); // ждем пока не будет открытых сокетов на запись или пока нас не разбудят генер нить
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Iterator<SelectionKey> iter = outputSelector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isWritable()) {
                    System.out.println("Writing in output thread");
                    ClientConnection c = (ClientConnection) key.attachment();
                    ByteBuffer buf = ByteBuffer.wrap((JsonHandler.createJson(c.rsaKey).toString() + "\n").getBytes(StandardCharsets.UTF_8));
                    SocketChannel ch = (SocketChannel) key.channel();
                    try {
                        int written = ch.write(buf);
                        System.out.println(c.name);
                        System.out.println("written=" + written);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        ch.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //key.interestOps(0); // or OP_READ if needed
                }
            }

            outputSelector.selectedKeys().clear();
            ClientConnection c;
            while ((c = outputQueue.poll()) != null) {
                SocketChannel clientChannel = c.clientChannel;
                System.out.println("Client received in output thread");
                try {
                    clientChannel.register(outputSelector, SelectionKey.OP_WRITE).attach(c);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            outputSelector.wakeup();
        }
    }
}
