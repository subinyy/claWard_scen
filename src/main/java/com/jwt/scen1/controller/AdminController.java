package com.jwt.scen1.controller;// AdminController.java
import com.jwt.scen1.util.jwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts; // Jwts를 사용하여 클레임을 추출하는 대신 jwtUtil.parsePayloadToMap을 사용
import org.springframework.beans.factory.annotation.Autowired; // Autowired 추가
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors; // Collectors 임포트 추가

@RestController
public class AdminController {

    @Autowired
    private jwtUtil jwtUtilInstance; // jwtUtil을 주입받음

    // 다운로드 가능한 파일 목록
    private static final List<String> DOWNLOADABLE_FILES = List.of("hint.txt", "downloaded_file.txt", "admin_report.pdf");
    private static final String FILE_BASE_PATH = "./src/main/resources/files/";

    @GetMapping("/admin/files")
    public ResponseEntity<?> getDownloadableFiles(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // 실제 운영 환경에서는 인증된 사용자만 파일 목록을 볼 수 있도록 해야 하지만, 워게임 편의상 인증 없이도 목록을 보여주도록 함
        // JWT 인증 우회 시나리오이므로, 파일 목록은 누구나 볼 수 있도록 설정
        return ResponseEntity.ok(Map.of("availableFiles", DOWNLOADABLE_FILES));
    }

    @GetMapping("/admin/download")
    public ResponseEntity<?> downloadFile(
        @RequestParam String file,
        @RequestHeader("Authorization") String authHeader) {
        // 1. Bearer {token} 파싱
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 토>큰이 필요합니다.");
        }

        String token = authHeader.replace("Bearer ", "").trim();

        // JWT 유효성 검증 및 alg:none 우회 처리
        if (!jwtUtilInstance.isTokenValid(token)) { // jwtUtil의 isTokenValid 호출
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못되거나 만료된 JWT입니다.");
        }

        Map<String, Object> claims;
        try {
            // isTokenValid에서 alg:none을 처리했으므로, 여기서는 페이로드만 파싱하여 클레임 추출
            claims = jwtUtil.parsePayloadToMap(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 페이로드 파싱 실패: " + e.getMessage());
        }

        System.out.println("claims = " + claims);
        System.out.println("admin field type = " + (claims.get("admin") != null ? claims.get("admin").getClass() : "null"));
        System.out.println("admin field value = " + claims.get("admin"));


        // admin 필드 확인
        boolean isAdmin = false;
        Object adminObj = claims.get("admin");
        if (adminObj instanceof Boolean) {
            isAdmin = (Boolean) adminObj;
        } else if (adminObj instanceof String) {
            isAdmin = Boolean.parseBoolean((String) adminObj);
        }

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자 권한이 필요합니다.");
        }

        /*
        // 파일 다운로드 - 일단 목록에 있는 것만 다운되게 했음
        if (!DOWNLOADABLE_FILES.contains(file)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("다운로드할 수 없는 파일입니다.");
        }
         */

        Path filePath = Paths.get(FILE_BASE_PATH, file).normalize();
        //Resource resource = new ClassPathResource("files/" + file);
        File resource = new File(filePath.toString());

        System.out.println(resource.getAbsolutePath());
        System.out.println(filePath);


        if (!resource.exists() || !resource.isFile()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일을 찾을 수 없거나 읽을 수 없습니다.");
        }

        try {
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // 기본값
            }

            byte[] fileBytes = Files.readAllBytes(filePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getName() + "\"")
                    .body(fileBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 다운로드 중 오류 발생: " + e.getMessage());
        }
    }
}