package ru.nsu.kolodina.keys;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class JsonHandler {

    public static JSONObject createJson(KeyPairRequest key) {
        JSONObject json = new JSONObject();

        json.put("Public Key", Base64.getEncoder().encodeToString(key.keyPair.getPublic().getEncoded()));
        json.put("Private Key", Base64.getEncoder().encodeToString(key.keyPair.getPrivate().getEncoded()));

        String certBase64;
        try {
            certBase64 = Base64.getEncoder().encodeToString(key.certificate.getEncoded());
        } catch (CertificateEncodingException e) {
            throw new RuntimeException(e);
        }
        json.put("Certificate", certBase64);
        return json;
    }

    public static KeyPairRequest parseJson(JSONObject json) {
        byte[] certBytes = Base64.getDecoder().decode(json.getString("Certificate"));
        X509Certificate certificate = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certBytes));
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }

        PrivateKey privateKey = null;
        byte[] keyBytes = null;
        keyBytes = Base64.getDecoder().decode(json.getString("Private Key"));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            privateKey = kf.generatePrivate(spec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        PublicKey publicKey = null;
        byte[] publicKeyBytes = Base64.getDecoder().decode(json.getString("Public Key"));
        X509EncodedKeySpec specPublicKey = new X509EncodedKeySpec(publicKeyBytes);
        try {
            publicKey = kf.generatePublic(specPublicKey);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        return new KeyPairRequest(new KeyPair(publicKey, privateKey), certificate);
    }
}
