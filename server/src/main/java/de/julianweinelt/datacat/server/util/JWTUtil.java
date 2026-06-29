package de.julianweinelt.datacat.server.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import de.julianweinelt.datacat.server.store.files.LocalStorage;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class JWTUtil {
    private final JWTVerifier verifier;
    private static JWTUtil instance;
    private final String secret;

    public JWTUtil(String secret) {
        instance = this;
        this.secret = secret;
        if (secret.isEmpty()) {
            String newSecret = CryptoUtil.generateSecret(20);
            secret = newSecret;
            LocalStorage.instance().getConfig().setJwtSecret(newSecret);
            LocalStorage.instance().saveConfig();
        }
        verifier = JWT.require(Algorithm.HMAC256(secret)).build();
    }

    public static JWTUtil instance() {
        return instance;
    }

    public String generateToken(UUID userID) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.MINUTE, LocalStorage.instance().getConfig().getTokenLifetime());
        Date expiration = calendar.getTime();
        return JWT.create()
                .withSubject(userID.toString())
                .withIssuer("datacat-store")
                .withClaim("scope", "authToken")
                .withNotBefore(now)
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(Algorithm.HMAC256(LocalStorage.instance().getConfig().getJwtSecret()));
    }

    public DecodedJWT decode(String token) {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            log.error("Failed to decode JWT token: {}", e.getMessage());
            return null;
        }
    }
}