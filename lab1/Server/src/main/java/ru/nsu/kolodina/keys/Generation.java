package ru.nsu.kolodina.keys;

import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class Generation {

    KeyPairGenerator generator;

    public Generation() {
        try {
            generator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        generator.initialize(2048);
    }

    public KeyPair generateKeys() {
        return generator.generateKeyPair();
    }

}
