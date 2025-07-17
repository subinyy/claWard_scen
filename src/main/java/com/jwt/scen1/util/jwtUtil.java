package com.jwt.scen1.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
public class jwtUtil {

    private final String SECRET = "sksmsroEhdajffpclsrnrkdjqtsp1234567889999";

    // 1. JWT Payload만 파싱 (alg:none 우회에 사용)
    public static Map<String, Object> parsePayloadToMap(String token) {
        String[] parts = token.split("\\.", -1);
        if (parts.length != 3) throw new IllegalArgumentException("Invalid JWT format");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(payloadJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Payload parsing failed: " + e.getMessage());
        }
    }

    // 2. JWT 생성 (HMAC-SHA256 사용)
    public String generateToken(String username, boolean isAdmin) {
        return Jwts.builder()
                .setSubject(username)
                .claim("user", username)
                .claim("admin", isAdmin)
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. JWT 유효성 체크 (alg:none 취약점 반영)
    public boolean isTokenValid(String token) {
        try {
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3) {
                System.out.println("잘못된 구조 (토큰 파트 개수 불일치)");
                return false;
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            System.out.println("Decoded Header: " + headerJson);

            // alg:none 취약점: signature 검증을 우회하도록 의도적 허용
            if (headerJson.contains("\"alg\":\"none\"")) {
                System.out.println("alg:none detected! Signature 검증 우회!");
                try {
                    parsePayloadToMap(token);
                    return true; // signature 없는 토큰 허용
                } catch (Exception e) {
                    System.out.println("alg:none 토큰 payload 파싱 실패: " + e.getMessage());
                    return false;
                }
            }

            // 정상 JWT는 서명 검증
            Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                    .parseClaimsJws(token);
            System.out.println("정상 서명 검증 성공");
            return true;
        } catch (SignatureException e) {
            System.out.println("서명 검증 실패: " + e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            System.out.println("토큰 만료: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception 발생: " + e.getMessage());
            return false;
        }
    }

    // 4. JWT Claims 추출 (alg:none 지원 포함)
    public Map<String, Object> extractClaims(String token) {
        String[] parts = token.split("\\.", -1);
        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
        if (headerJson.contains("\"alg\":\"none\"")) {
            // alg:none이면 signature 없이 payload만 파싱
            return parsePayloadToMap(token);
        }
        // 서명 검증이 필요한 경우
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (Exception e) {
            System.out.println("Failed to extract claims: " + e.getMessage());
            return null;
        }
    }
}
