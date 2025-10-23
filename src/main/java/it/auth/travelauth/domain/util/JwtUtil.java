package it.auth.travelauth.domain.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        Date issuedAt = new Date();
        Date expiration = new Date(System.currentTimeMillis() + 86400000); // 1 giorno

        // Formattazione italiana
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String issuedAtFormatted = sdf.format(issuedAt);
        String expirationFormatted = sdf.format(expiration);

        System.out.println("Token generato per " + username);
        System.out.println("Data emissione: " + issuedAtFormatted);
        System.out.println("Data scadenza: " + expirationFormatted);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getKey())
                .compact();
    }

    // Estrae il nome utente dal token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Verifica se il token è valido
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    // Controlla se il token è scaduto
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    // Estrae tutti i claims dal token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey()) // usa la chiave generata dinamicamente
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Metodo di validazione completo (opzionale)
    public String validateToken(String token) {
        if (isTokenValid(token)) {
            return extractUsername(token);
        }
        return null;
    }
}
