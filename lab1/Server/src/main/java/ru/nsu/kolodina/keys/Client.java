package ru.nsu.kolodina.keys;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Client {
    String name;
    List<ClientConnection> connections;
    Boolean ready;
}
