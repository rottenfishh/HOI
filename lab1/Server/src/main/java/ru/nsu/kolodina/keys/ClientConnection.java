package ru.nsu.kolodina.keys;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

@RequiredArgsConstructor
public class ClientConnection {
    @NonNull
    Socket clientSocket;
    @NonNull
    BufferedReader in;
    @NonNull
    PrintWriter out;
    @NonNull
    @Setter
    String name;
    @Setter
    String rsaKey;
    Boolean ready;
}