package ru.nsu.kolodina.keys;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@RequiredArgsConstructor
public class Client {
    @NonNull
    String host;
    @NonNull
    int port;
    @NonNull
    String name;
    PrintWriter out;
    BufferedReader in;
    Socket clientSocket;
    boolean connectionClosed = false;

    public Socket connectToServer(int retries) {
        try {
            return new Socket(host, port);
        } catch (IOException e) {
            System.out.println("Error in connecting to " + host + ":" + port);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                System.out.println("bububububu");
            }
        }
        return null;
    }

    public void startConnection() {
        int retries = 10;
        this.clientSocket = connectToServer(retries);
        if (clientSocket == null) {
            return;
        }
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Connected to " + this.host + ":" + this.port);
        out.println(name);
    }

    public String getKey() {
        String response;
        try {
            String line = in.readLine();
            if (line == null) {
                stopConnection();
                return null;
            }
            response = line; // wrap up logic here later
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        connectionClosed = true;
    }

    public void saveKeys() {
        //implement
    }
    public void run() {
        startConnection();
        System.out.println("Connection established");
        do {
            System.out.println("trying to get key");
            String keys = getKey();
            System.out.println("got key");
            if (keys == null) {
                break;
            }
        } while (!connectionClosed);
    }
    public static void main(String[] args) {
        Client client = new Client("localhost", 5555, "Bebe");
        client.run();
    }
}
