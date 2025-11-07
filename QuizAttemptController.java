package com.mavpal.controller;

import com.mavpal.entity.User;
import com.mavpal.repository.UserRepository;
import com.mavpal.service.QuizService;
import com.mavpal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sets/{setId}/attempts")
@CrossOrigin(origins = "http://localhost:3000")
public class QuizAttemptController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private Integer getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }

    @PostMapping
    public ResponseEntity<?> submitAttempt(@PathVariable Integer setId,
                                            @RequestBody Map<String, Object> request,
                                            @RequestHeader("Authorization") String authHeader) {
        Integer userId = getUserIdFromToken(authHeader);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> answers = (List<Map<String, Object>>) request.get("answers");
        Integer durationMs = (Integer) request.get("durationMs");

        if (answers == null || durationMs == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing required fields: answers, durationMs"));
        }

        try {
            Map<String, Object> result = quizService.gradeQuiz(setId, userId, answers, durationMs);

            // Get updated user info
            User user = userRepository.findById(userId)
                    .orElse(null);

            if (user != null) {
                result.put("newXpTotal", user.getXp());
                result.put("level", user.getLevel());
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
