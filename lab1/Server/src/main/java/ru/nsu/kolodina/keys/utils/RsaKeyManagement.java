package ru.nsu.kolodina.keys.utils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import ru.nsu.kolodina.keys.KeyPairRequest;

public class RsaKeyManagement {

    KeyPairGenerator generator;

    public RsaKeyManagement() {
        try {
            generator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        generator.initialize(2048);
    }

    public static PrivateKey loadPrivateKey(String filename) {
        byte[] keyBytes = null;
        try {
            keyBytes = Files.readAllBytes(new File(filename).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try {
            return kf.generatePrivate(spec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public KeyPairRequest generateClientKeys(String clientName) {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        keyGen.initialize(8192);
        KeyPair keyPair = keyGen.generateKeyPair();

        X500Name issuerName = new X500Name("CN=server");
        X500Name subjectName = new X500Name("CN=" + clientName);
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date(System.currentTimeMillis() - 1000);
        Date notAfter = new Date(System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000L)); // valid for 1 year

        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(issuerName, serial, notBefore, notAfter, subjectName, SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));

        PrivateKey serverKey = loadPrivateKey("serverKey");
        ContentSigner signer = null;
        try {
            signer = new JcaContentSignerBuilder("SHA256withRSA").build(serverKey);
        } catch (OperatorCreationException e) {
            throw new RuntimeException(e);
        }
        X509Certificate certificate = null;
        try {
            certificate = new JcaX509CertificateConverter().getCertificate(certBuilder.build(signer));
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }

        return new KeyPairRequest(keyPair, certificate);
    }
}
