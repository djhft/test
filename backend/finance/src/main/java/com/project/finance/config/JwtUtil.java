package com.project.finance.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expireSeconds}")
    private long expireSeconds;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId) {
        try {
            if (secret == null || secret.length() < 32) {
                throw new IllegalArgumentException("Invalid secret key");
            }
            Date now = new Date();
            Date expiry = new Date(now.getTime() + expireSeconds * 1000);

            return Jwts.builder()
                    .subject(String.valueOf(userId))
                    .issuedAt(now)
                    .expiration(expiry)
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            System.out.println("Error generating token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 为用户详情生成token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireSeconds * 1000);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public Long parseUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.parseLong(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Error parsing token: " + e.getMessage());
            return null;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid token: " + e.getMessage());
            return false;
        }
    }

    // 刷新token
// 刷新token
    public String refreshToken(String token) {
        try {
            final Claims originalClaims = getAllClaimsFromToken(token);
            String subject = originalClaims.getSubject();
            Map<String, Object> claims = new HashMap<>();

            // 复制除时间戳外的所有claims
            originalClaims.forEach((key, value) -> {
                if (!key.equals("iat") && !key.equals("exp")) {
                    claims.put(key, value);
                }
            });

            // 生成新的token
            return createToken(claims, subject);
        } catch (Exception e) {
            System.out.println("Error refreshing token: " + e.getMessage());
            return null;
        }
    }

}
