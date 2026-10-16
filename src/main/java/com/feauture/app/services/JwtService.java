package com.feauture.app.services;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiryMs;

    public JwtService(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiry_ms}") long expiryMs) {

        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiryMs = expiryMs;

    }

    public String generateToken(String id) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiryMs);
        return Jwts.builder().subject(id).issuedAt(now).expiration(expiry).signWith(key).compact();
    }

    public Optional<String> verifyToken(String token) {

        try {
            String subject = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();

            return Optional.ofNullable(subject);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            return Optional.empty();

        }

    }

}
