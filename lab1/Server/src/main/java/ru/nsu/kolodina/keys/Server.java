package ru.nsu.kolodina.keys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    Map<String, ClientConnection> clients = new HashMap<>();
    ServerSocket serverSocket;
    Generation keyGenerator = new Generation();

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
                if (clients.containsKey(name)) {
                    out.println(clients.get(name).rsaKey);
                } else {
                    ClientConnection client = new ClientConnection(clientSocket, in, out, name);
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