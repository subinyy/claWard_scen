package com.jwt.scen1.controller; // AuthController.java
import com.jwt.scen1.util.jwtUtil;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; // 동시성 문제를 고려한 HashMap

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired; // jwtUtil 주입을 위해 추가

@RestController
public class AuthController {
    // 예시용 secret (실서비스에서는 .env 등에서 관리)
    private static final String SECRET_KEY = "very_secret_for_demo"; // jwtUtil의 SECRET과 동일하게 설정
    // 토큰 30분 유효
    private static final long EXPIRATION = 30 * 60 * 1000;

    // 사용자 정보를 저장할 Map (메모리상` 임시 저장소)
    private final Map<String, String> users = new ConcurrentHashMap<>();

    @Autowired
    private jwtUtil jwtUtilInstance; // jwtUtil 인스턴스 주입

    // 회원가입 엔드포인트 추가
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "사용자 이름과 비밀번호를 입력해주세요."));
        }

        if (users.containsKey(username)) {
            return ResponseEntity.status(409).body(Map.of("message", "이미 존재하는 사용자 이름입니다."));
        }

        // admin 아이디 가입 불가
        if ("admin".equalsIgnoreCase(username)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "admin 아이디는 가입할 수 없습니다."));
        }

        // 비밀번호 약간 복잡하게
        // 영어, 숫자, 특수문자 각 1개 이상, 8자 이상
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$")) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "비밀번호는 8자 이상, 영문/숫자/특수문자를 모두 포함해야 합니다."));
        }

        // 실제 서비스에서는 비밀번호를 해싱하여 저장해야 합니다.
        users.put(username, password);
        System.out.println("새로운 사용자 등록: " + username);
        return ResponseEntity.ok(Map.of("message", "회원가입 성공!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");

        // 등록된 사용자인지 확인 (실제론 DB 연동)
        if (!users.containsKey(username) || !users.get(username).equals(password)) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 실패: 잘못된 사용자 이름 또는 비밀번호."));
        }

        // 일반 계정은 admin: false
        boolean isAdmin = false;
        if ("admin".equals(username)) {
            isAdmin = true; // 예시: admin 계정만 관리자로
        }

        String token = jwtUtilInstance.generateToken(username, isAdmin);

        System.out.println("사용자 " + username + " 로그인 성공. 토큰 발급: " + token);
        return ResponseEntity.ok(Map.of("token", token));
    }
}