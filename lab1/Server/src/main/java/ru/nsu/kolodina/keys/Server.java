package ru.nsu.kolodina.keys;

import lombok.AllArgsConstructor;
import ru.nsu.kolodina.keys.entity.ClientConnection;
import ru.nsu.kolodina.keys.entity.KeyState;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor
public class Server {
    int port;
    int numOfInputThreads;
    int numOfGeneratingThreads;
    int numOfOutputThreads;

    public void startServer() {
        try {
            Map<String, KeyState> clients = new ConcurrentHashMap<>();
            final LinkedBlockingQueue<ClientConnection> requestsQueue = new LinkedBlockingQueue<>();
            final LinkedBlockingQueue<ClientConnection> outputQueue = new LinkedBlockingQueue<>();
            Selector serverSelector = Selector.open();
            Selector outputSelector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("localhost", port));
            serverSocket.configureBlocking(false);
            serverSocket.register(serverSelector, SelectionKey.OP_ACCEPT);
            for (int i = 0; i < numOfInputThreads; i++) {
                Thread t = new Thread(new InputThread(serverSelector, serverSocket,requestsQueue));
                t.start();
            }
            for (int i = 0; i < numOfGeneratingThreads; i++) {
                Thread t = new Thread(new GeneratingThread(outputSelector, requestsQueue, outputQueue));
                t.start();
            }
            for (int i = 0; i < numOfOutputThreads; i++) {
                Thread t = new Thread(new OutputThread(outputQueue, outputSelector));
                t.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5555, 1, 7, 2);
        server.startServer();
    }
}
