package ru.nsu.kolodina.keys;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor
public class InputThread implements Runnable {
    Map<String, KeyState> clients;
    final Selector serverSelector;
    final ServerSocketChannel serverSocket;
    final Selector outputSelector; // selector of output would be needed everywhere
    @NonNull
    final LinkedBlockingQueue<ClientConnection> requestsQueue;
    @NonNull
    final LinkedBlockingQueue<ClientConnection> outputQueue;
    @Override
    public void run() {
        while (true) {
            try {
                serverSelector.select();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (SelectionKey key : serverSelector.selectedKeys()) {
                if (key.isAcceptable()) {
                    try {
                        SocketChannel client = serverSocket.accept();
                        client.configureBlocking(false);
                        client.register(serverSelector, SelectionKey.OP_READ);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (key.isReadable()) {
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    try {
                        int r = clientChannel.read(buffer);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String name = new String(buffer.array());
                    ClientConnection c = new ClientConnection(name, clientChannel);
                    key.attach(c);
                    // sent client connection to request list to generating thread
                }
            }
            serverSelector.selectedKeys().clear();
        }
    }
}
