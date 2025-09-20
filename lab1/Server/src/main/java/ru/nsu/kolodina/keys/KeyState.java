package ru.nsu.kolodina.keys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class KeyState {
    volatile KeyPairRequest key;
    AtomicBoolean ready = new AtomicBoolean(false);
    AtomicBoolean generating = new AtomicBoolean(false);
    List<ClientConnection> keyClients = Collections.synchronizedList(new ArrayList<>());;
}
