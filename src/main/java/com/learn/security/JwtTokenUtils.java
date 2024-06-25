package com.learn.security;

import com.learn.model.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// 30 test cases
@Component
public class JwtTokenUtils {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtils.class);
    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hour

    @Value("${app.jwt.secret:1A2B3C4D5E6F7A8B9C0D1E2F3A4B5C6D7E8F9A0B1C2D3E4F5A6B7C8D9E0F1A2B3C4D5E6F7A8B9C0D1E2F3A4B5C6D7E8F9A0B1C2D3E4F5A6B7C8D9E0F1A2B3C4D5E6F7A8B9C0D1E2F3A4B5C6D7E8F9A0B1C2D}")
    private String SECRET_KEY;

    public boolean validateAccessToken(String token) {
        try {
            // Create key
            Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.info("JWT expired. Error {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.info("Token is null, empty or only whitespace: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.info("JWT is invalid");
        } catch (UnsupportedJwtException ex) {
            LOGGER.info("JWT is not supported");
        }
        return false;
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessToken(User user) {
        try {
            return Jwts.builder()
                    .setSubject(String.format("%s", user.getEmail()))
                    .setIssuer(user.getEmail())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

    public Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
