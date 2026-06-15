package com.cinema.api.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    // Endpoints accessible without token
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/users/register",
            "/api/users/login",
            "/fallback"
    );

    // Endpoints accessible only to ADMIN
    private static final List<String> ADMIN_POST_ENDPOINTS = List.of(
            "/api/movies",
            "/api/movies/screenings",
            "/api/cinemas",
            "/api/cinemas/halls"
    );

    private static final List<String> ADMIN_DELETE_ENDPOINTS = List.of(
            "/api/movies",
            "/api/movies/screenings",
            "/api/cinemas",
            "/api/users"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        HttpMethod method = exchange.getRequest().getMethod();

        // Allow public endpoints
        if (PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        // Check Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = extractAllClaims(token);
            String role = claims.get("role", String.class);

            // Check ADMIN-only endpoints
            boolean isAdminRequired = false;

            if (HttpMethod.POST.equals(method)) {
                isAdminRequired = ADMIN_POST_ENDPOINTS.stream().anyMatch(path::startsWith);
            } else if (HttpMethod.DELETE.equals(method)) {
                isAdminRequired = ADMIN_DELETE_ENDPOINTS.stream().anyMatch(path::startsWith);
            } else if (HttpMethod.GET.equals(method)) {
                // Only GET /api/users (list all) requires ADMIN, not /api/users/{id}
                isAdminRequired = path.equals("/api/users");
            } else if (HttpMethod.PUT.equals(method)) {
                isAdminRequired = path.startsWith("/api/cinemas/seats");
            }

            if (isAdminRequired && !"ADMIN".equals(role)) {
                return onError(exchange, HttpStatus.FORBIDDEN, "Access denied: insufficient permissions");
            }

            return chain.filter(exchange);

        } catch (Exception e) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(body);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = hexStringToByteArray(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}