package com.jwt.scen1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class HomeController {
    @GetMapping("/news")
    public ResponseEntity<?> newsList() {
        List<Map<String, String>> articles = List.of(
                Map.of("title", "보안 위협 증가", "content", "최근 사이버 공격이 증가하고 있습니다.")
        );
        return ResponseEntity.ok(articles);
    }
}