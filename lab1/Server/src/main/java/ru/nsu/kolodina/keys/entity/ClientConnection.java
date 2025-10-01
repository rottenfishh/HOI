package ru.nsu.kolodina.keys.entity;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.nsu.kolodina.keys.KeyPairRequest;

import java.nio.channels.SocketChannel;

@RequiredArgsConstructor
public class ClientConnection {
    @NonNull
    public String name;
    @NonNull
    public SocketChannel clientChannel;
    @Setter
    public KeyPairRequest rsaKey;
}