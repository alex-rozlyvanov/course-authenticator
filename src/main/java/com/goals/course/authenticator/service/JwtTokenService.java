package com.goals.course.authenticator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.dto.UserDTO;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final ObjectMapper objectMapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    @Value("${app.jwt.access.secret}")
    private String accessTokenSecret;
    @Value("${app.jwt.access.expiration}")
    private Duration accessTokenExpiration;
    @Value("${app.jwt.issuer}")
    private String jwtIssuer;
    @Value("${app.jwt.refresh.expiration}")
    private Duration refreshTokenExpiration;
    @Value("${app.jwt.refresh.secret}")
    private String refreshTokenSecret;

    public String generateAccessToken(final User user) {
        final var claims = buildClaims(user);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration.toMillis()))
                .signWith(SignatureAlgorithm.HS256, accessTokenSecret)
                .addClaims(claims)
                .compact();
    }

    private Map<String, Object> buildClaims(final User user) {
        final var roles = user.getRoles();
        return Map.of("userId", user.getId().toString(), "roles", roles);
    }

    public UUID getUserId(final String token) {
        return UUID.fromString(Jwts.parser()
                .setSigningKey(accessTokenSecret)
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class));
    }

    public String getUsername(final String token) {
        return Jwts.parser()
                .setSigningKey(accessTokenSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Date getExpirationDate(final String token) {
        return Jwts.parser()
                .setSigningKey(accessTokenSecret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public UserDTO getUserDTO(final String token) {
        final var body = Jwts.parser()
                .setSigningKey(accessTokenSecret)
                .parseClaimsJws(token)
                .getBody();
        final var userId = UUID.fromString(body.get("userId", String.class));
        final var roles = getRoles(body);

        return UserDTO.builder()
                .id(userId)
                .roles(roles)
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<RoleDTO> getRoles(final Claims claims) {
        final List<Map<String, Object>> rolesRawList = claims.get("roles", List.class);

        return rolesRawList.stream()
                .map(r -> objectMapper.convertValue(r, RoleDTO.class))
                .toList();

    }

    public boolean validate(final String token) {
        try {
            Jwts.parser().setSigningKey(accessTokenSecret).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token - {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token - {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token - {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty - {}", ex.getMessage());
        }
        return false;
    }

    public String generateRefreshToken(final User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration.toMillis()))
                .signWith(SignatureAlgorithm.HS512, refreshTokenSecret)
                .compact();
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpiration.toMillis();
    }

}
