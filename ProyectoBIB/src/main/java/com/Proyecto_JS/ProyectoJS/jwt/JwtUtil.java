package com.Proyecto_JS.ProyectoJS.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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

    // Clave secreta para firmar tokens (en producción usar variable de entorno)
    private static final String SECRET = "libros_como_alas_secret_key_2024_proyecto_final_universidad_tecnologica_peru";
    
    // Generar clave secreta a partir del string
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    
    // Tiempo de expiración: 10 horas en milisegundos
    private static final long JWT_TOKEN_VALIDITY = 10 * 60 * 60 * 1000;

    /**
     * Extrae el username (email) del token JWT
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verifica si el token ha expirado
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un token JWT para un usuario autenticado
     * 
     * @param userDetails - Detalles del usuario autenticado
     * @return String - Token JWT generado
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crea el token JWT con claims, subject, fecha de emisión y expiración
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject) // El subject es el email del usuario
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Valida el token JWT
     * 
     * @param token - Token JWT a validar
     * @param userDetails - Detalles del usuario autenticado
     * @return Boolean - true si el token es válido, false en caso contrario
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
