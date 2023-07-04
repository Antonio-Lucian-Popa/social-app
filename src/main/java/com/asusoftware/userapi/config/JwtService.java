package com.asusoftware.userapi.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "HeBfIj7l25aq6hVJSHxnT1cMpSJoL5gV";

    /**
     * Get the sign in key
     * @return sign in key
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // Subject will be the email(username) of the user
    }

    /**
     * Extract a claim from the jwt token
     * @param token jwt token
     * @param claimsResolver claims resolver
     * @param <T> type of the claim
     * @return claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generate a jwt token
     * @param userDetails user details
     * @return jwt token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generate a jwt token
     * @param extraClaims extra claims that we want to add to the token
     * @param userDetails user details
     * @return jwt token
     */
    public String generateToken(Map<String, Objects> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // check if the token is expired
                        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                        .compact(); // generate and return the token
    }

    /**
     * Check if the token is valid
     * @param token jwt token
     * @param userDetails user details
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // Check if the token is expired
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration); // Expiration will be the expiration date of the token
    }

    /**
     * Check if the token is valid
     * @param token jwt token
     * @return true if the token is valid, false otherwise
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJwt(token)
                .getBody();
    }

    /**
     * Get the sign in key
     * @return sign in key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
