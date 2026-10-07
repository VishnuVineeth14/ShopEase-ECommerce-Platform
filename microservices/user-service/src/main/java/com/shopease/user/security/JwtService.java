package com.shopease.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret:}")
    private String configuredSecret;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpiration;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        if (configuredSecret != null && !configuredSecret.isBlank()) {
            try {
                this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(configuredSecret.trim()));
                return;
            } catch (Exception e) {
                this.signingKey = Keys.hmacShaKeyFor(configuredSecret.trim().getBytes(StandardCharsets.UTF_8));
                return;
            }
        }
        this.signingKey = resolveLocalSharedKey();
    }

    private SecretKey resolveLocalSharedKey() {
        try {
            Path sharedDir = Paths.get(System.getProperty("user.home"), ".shopease");
            Files.createDirectories(sharedDir);
            Path keyPath = sharedDir.resolve(".jwt_secret");

            if (Files.exists(keyPath)) {
                String base64Key = Files.readString(keyPath).trim();
                if (!base64Key.isBlank()) {
                    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Key));
                }
            }

            // Generate a secure 256-bit HMAC key
            SecretKey generatedKey = Jwts.SIG.HS256.key().build();
            String base64 = Encoders.BASE64.encode(generatedKey.getEncoded());
            Files.writeString(keyPath, base64);
            return generatedKey;
        } catch (Exception e) {
            // Memory fallback
            return Jwts.SIG.HS256.key().build();
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails, String userId, String role) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userId);
        extraClaims.put("role", role);
        return generateToken(extraClaims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(signingKey)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
