// jwtUtil.java (수정 부분)

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

    public static Map<String, Object> parsePayloadToMap(String token) {
        String[] parts = token.split("\\.", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT format: Not 3 parts");
        }
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(payloadJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Payload parsing failed: " + e.getMessage());
        }
    }

    public String generateToken(String username, boolean isAdmin) {
        return Jwts.builder()
                .setSubject(username)
                .claim("user", username)
                .claim("admin", isAdmin)
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            String[] parts = token.split("\\.", -1);

            if (parts.length != 3) {
                System.out.println("1. 잘못된 구조 (토큰 파트 개수 불일치)");
                return false;
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            System.out.println("2. Decoded Header: " + headerJson);

            if (headerJson.contains("\"alg\":\"none\"")) {
                System.out.println("3. alg:none detected! Bypassing signature check!");
                try {
                    // 이 부분을 수정합니다.
                    jwtUtil.parsePayloadToMap(token); // <<< 여기를 `jwtUtil.parsePayloadToMap(token)`로 수정
                    return true;
                } catch (Exception e) {
                    System.out.println("Payload parsing failed for alg:none token: " + e.getMessage());
                    return false;
                }
            }

            Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                    .parseClaimsJws(token);
            System.out.println("4. 정상 서명 검증 성공");
            return true;
        } catch (SignatureException e) {
            System.out.println("5. 서명 검증 실패: " + e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            System.out.println("5. 토큰 만료: " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.out.println("5. 잘못된 JWT 형식: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("5. Exception 발생: " + e.getMessage());
            return false;
        }
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println("Failed to extract claims: " + e.getMessage());
            return null;
        }
    }
}