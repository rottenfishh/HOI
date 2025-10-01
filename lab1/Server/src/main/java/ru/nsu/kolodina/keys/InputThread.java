package ru.nsu.kolodina.keys;

import lombok.AllArgsConstructor;
import ru.nsu.kolodina.keys.entity.ClientConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor
public class InputThread implements Runnable {
    final Selector serverSelector;
    final ServerSocketChannel serverSocket;
    final LinkedBlockingQueue<ClientConnection> requestsQueue;

    public String trimInput(ByteBuffer input) {
        StringBuilder res = new StringBuilder();
        for (byte b : input.array()) {
            if (b == '\0') break;
            res.append((char) b);
        }
        return res.toString();
    }

    public void acceptClient(SelectionKey key) {
        try {
            if (key.isAcceptable()) {
                SocketChannel client = serverSocket.accept();
                System.out.println("accepted client");
                if (client != null) {
                    client.configureBlocking(false);
                    client.register(serverSelector, SelectionKey.OP_READ);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readFromClient(SelectionKey key) {
        if (key.isReadable()) {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            try {
                int r = clientChannel.read(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String name = trimInput(buffer);
            System.out.println("Client " + name + " received");

            ClientConnection c = new ClientConnection(name, clientChannel);
            key.attach(c);
            requestsQueue.offer(c);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                serverSelector.select();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Iterator<SelectionKey> iter = serverSelector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                acceptClient(key);

                readFromClient(key);
            }
        }
    }
}
