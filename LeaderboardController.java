package com.mavpal.controller;

import com.mavpal.service.LeaderboardService;
import com.mavpal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "http://localhost:3000")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getLeaderboard(@RequestParam(defaultValue = "50") int top,
                                             @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Integer currentUserId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                currentUserId = jwtUtil.extractUserId(token);
            } catch (Exception e) {
                // Invalid token, continue without user context
            }
        }

        var result = leaderboardService.getTopUsers(top, currentUserId);
        return ResponseEntity.ok(result);
    }
}
