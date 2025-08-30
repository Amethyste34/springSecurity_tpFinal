package com.example.demo.services;

import com.example.demo.models.UserApp;
import com.example.demo.repositories.UserAppRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Service
public class JwtService extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.cookie_name}")
    private String cookieName;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000; // 5 heures

    @Autowired
    private UserAppRepository userAppRepository;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(cookieName))
                    .map(Cookie::getValue)
                    .forEach(token -> {
                        try {
                            Claims claims = Jwts.parserBuilder()
                                    .setSigningKey(getSigningKey())
                                    .build()
                                    .parseClaimsJws(token)
                                    .getBody();

                            Optional<UserApp> optUserApp = userAppRepository.findByUsername(claims.getSubject());
                            if (optUserApp.isEmpty()) {
                                return;
                            }
                            UserApp userApp = optUserApp.get();

                            // Ajout du rôle dans le context Spring
                            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + claims.get("role"));
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(userApp, null, List.of(authority));
                            SecurityContextHolder.getContext().setAuthentication(auth);

                        } catch (Exception e) {
                            // supprime cookie invalide
                            Cookie expiredCookie = new Cookie(cookieName, null);
                            expiredCookie.setPath("/");
                            expiredCookie.setHttpOnly(true);
                            expiredCookie.setMaxAge(0);
                            response.addCookie(expiredCookie);
                        }
                    });
        }

        filterChain.doFilter(request, response);
    }

    public Boolean validateToken(String token, UserApp userApp) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String generateToken(UserApp userApp) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userApp.getUsername());
        claims.put("role", userApp.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userApp.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public ResponseCookie createAuthenticationToken(UserApp userApp) {
        String token = generateToken(userApp);
        return ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .path("/")
                .build();
    }
}