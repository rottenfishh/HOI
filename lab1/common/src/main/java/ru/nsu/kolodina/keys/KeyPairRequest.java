package ru.nsu.kolodina.keys;

import lombok.AllArgsConstructor;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

@AllArgsConstructor
public class KeyPairRequest {
    KeyPair keyPair;
    X509Certificate certificate;
}
