package ru.nsu.kolodina.keys;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class KeyGeneration {

    KeyPairGenerator generator;

    public KeyGeneration() {
        try {
            generator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        generator.initialize(2048);
    }

    public KeyPairRequest generateKeys(String clientName) {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        keyGen.initialize(8192);
        KeyPair keyPair = keyGen.generateKeyPair();

        // Set certificate details
        X500Name issuerName = new X500Name("CN=server");
        X500Name subjectName = new X500Name("CN=" + clientName);
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date(System.currentTimeMillis() - 1000);
        Date notAfter = new Date(System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000L)); // valid for 1 year

        // Create certificate
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(issuerName, serial, notBefore, notAfter, subjectName, SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));

        // Sign the certificate
        ContentSigner signer = null;
        try {
            signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        } catch (OperatorCreationException e) {
            throw new RuntimeException(e);
        }
        X509Certificate certificate = null;
        try {
            certificate = new JcaX509CertificateConverter().getCertificate(certBuilder.build(signer));
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }

        // Output the certificate
        System.out.println(certificate);
        return new KeyPairRequest(keyPair, certificate);
    }
}
