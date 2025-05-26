package com.afrosofttech.rest_jwt_demo.security;

import com.afrosofttech.rest_jwt_demo.util.KeyPairGenerator;
import com.nimbusds.jose.jwk.RSAKey;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

public class Jwks {
    private Jwks(){}
    public static RSAKey generateRSAKey() {
            // Generate RSA key pair
            KeyPair keyPair = KeyPairGenerator.generateRSAKey();
            // Convert to RSA Public and Private keys
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            // Return an RSAKey via nimbus-jose-jwt library
            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString()) // Optionally, set a unique Key ID (kid)
                    .build();
    }
}
