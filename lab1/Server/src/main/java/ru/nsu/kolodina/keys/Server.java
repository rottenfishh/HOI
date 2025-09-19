package ru.nsu.kolodina.keys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    Map<String, List<ClientConnection>> clients = new HashMap<>();
    ServerSocket serverSocket;
    KeyGeneration keyGenerator = new KeyGeneration();
    LinkedBlockingQueue<ClientConnection> requestsQueue = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<ClientConnection> outputQueue = new LinkedBlockingQueue<>();

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(30000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void acceptClients() {
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(1000000);
            } catch (IOException e) {
                break;
            }
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String name = in.readLine();
                ClientConnection client = new ClientConnection(clientSocket, in, out, name);
                if (clients.containsKey(name)) {
                    if (!clients.get(name).getFirst().ready) {
                        clients.get(name).add(client); // if not ready, add connection to the list of this client's name
                    } else { //else put in queue for output

                    }
                    // also need to handle if key still generating
                } else {
                    clients.putIfAbsent(name, client);
                    //some waiting for the keys logic
                }
            } catch (IOException e) {
                System.out.println("Error while accepting clients");
                break;
            }
        }
    }


}