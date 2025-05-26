package com.afrosofttech.rest_jwt_demo.util;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class KeyPairGenerator {
    public static KeyPair generateRSAKey() {
        try {
            java.security.KeyPairGenerator keyPairGenerator = java.security.KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
