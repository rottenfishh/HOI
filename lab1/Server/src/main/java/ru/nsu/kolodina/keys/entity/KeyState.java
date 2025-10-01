package ru.nsu.kolodina.keys.entity;

import ru.nsu.kolodina.keys.KeyPairRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static ru.nsu.kolodina.keys.entity.KeyState.State.REQUESTED;

public class KeyState {

    public volatile KeyPairRequest key;
    public AtomicReference<State> state = new AtomicReference<>(REQUESTED);
    public List<ClientConnection> keyClients = Collections.synchronizedList(new ArrayList<>());

    public enum State {
        REQUESTED,
        GENERATING,
        READY;
    }
}