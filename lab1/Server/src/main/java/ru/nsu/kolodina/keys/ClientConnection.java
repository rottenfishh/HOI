package ru.nsu.kolodina.keys;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.SocketChannel;

@RequiredArgsConstructor
public class ClientConnection {
    @NonNull
    String name;
    @NonNull
    SocketChannel clientChannel;
    @Setter
    KeyPairRequest rsaKey;
    Boolean ready;
}