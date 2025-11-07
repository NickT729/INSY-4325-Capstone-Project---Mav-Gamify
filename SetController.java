package com.mavpal.controller;

import com.mavpal.entity.Set;
import com.mavpal.repository.SetRepository;
import com.mavpal.service.XPService;
import com.mavpal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sets")
@CrossOrigin(origins = "http://localhost:3000")
public class SetController {

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private XPService xpService;

    private Integer getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }

    @PostMapping
    public ResponseEntity<?> createSet(@RequestBody Map<String, Object> request, 
                                       @RequestHeader("Authorization") String authHeader) {
        Integer userId = getUserIdFromToken(authHeader);

        Set set = new Set();
        set.setTitle((String) request.get("title"));
        set.setDescription((String) request.get("description"));
        set.setSubject((String) request.get("subject"));
        set.setVisibility((String) request.getOrDefault("visibility", "private"));
        set.setType((String) request.get("type")); // "flashcard" or "quiz"
        set.setCreatedBy(userId);
        set.setCreatedAt(LocalDateTime.now().toString());

        set = setRepository.save(set);

        // Award XP for creating a set (100 XP)
        int xpEarned = xpService.awardXp(userId, 100, "set_created", set.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("id", set.getId());
        response.put("title", set.getTitle());
        response.put("description", set.getDescription());
        response.put("subject", set.getSubject());
        response.put("visibility", set.getVisibility());
        response.put("type", set.getType());
        response.put("createdBy", set.getCreatedBy());
        response.put("createdAt", set.getCreatedAt());
        response.put("xpEarned", xpEarned);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getSets(@RequestParam(required = false) String subject,
                                     @RequestParam(required = false) Integer createdBy,
                                     @RequestParam(required = false) String visibility,
                                     @RequestHeader("Authorization") String authHeader) {
        Integer userId = getUserIdFromToken(authHeader);
        List<Set> sets;

        if (subject != null && !subject.isEmpty()) {
            sets = setRepository.findPublicOrUserSetsBySubject(userId, subject);
        } else if (createdBy != null) {
            sets = setRepository.findByCreatedBy(createdBy);
        } else if ("public".equals(visibility)) {
            sets = setRepository.findByVisibility("public");
        } else {
            sets = setRepository.findPublicOrUserSets(userId);
        }

        List<Map<String, Object>> response = sets.stream().map(set -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", set.getId());
            map.put("title", set.getTitle());
            map.put("description", set.getDescription());
            map.put("subject", set.getSubject());
            map.put("visibility", set.getVisibility());
            map.put("type", set.getType());
            map.put("createdBy", set.getCreatedBy());
            map.put("createdAt", set.getCreatedAt());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{setId}")
    public ResponseEntity<?> getSetById(@PathVariable Integer setId) {
        Set set = setRepository.findById(setId)
                .orElse(null);

        if (set == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Set not found"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", set.getId());
        response.put("title", set.getTitle());
        response.put("description", set.getDescription());
        response.put("subject", set.getSubject());
        response.put("visibility", set.getVisibility());
        response.put("type", set.getType());
        response.put("createdBy", set.getCreatedBy());
        response.put("createdAt", set.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{setId}")
    public ResponseEntity<?> updateSet(@PathVariable Integer setId,
                                       @RequestBody Map<String, Object> request,
                                       @RequestHeader("Authorization") String authHeader) {
        Integer userId = getUserIdFromToken(authHeader);
        Set set = setRepository.findById(setId)
                .orElse(null);

        if (set == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Set not found"));
        }

        if (!set.getCreatedBy().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Not authorized to update this set"));
        }

        if (request.containsKey("title")) {
            set.setTitle((String) request.get("title"));
        }
        if (request.containsKey("description")) {
            set.setDescription((String) request.get("description"));
        }
        if (request.containsKey("subject")) {
            set.setSubject((String) request.get("subject"));
        }
        if (request.containsKey("visibility")) {
            set.setVisibility((String) request.get("visibility"));
        }

        set = setRepository.save(set);

        Map<String, Object> response = new HashMap<>();
        response.put("id", set.getId());
        response.put("title", set.getTitle());
        response.put("description", set.getDescription());
        response.put("subject", set.getSubject());
        response.put("visibility", set.getVisibility());
        response.put("type", set.getType());
        response.put("createdBy", set.getCreatedBy());
        response.put("createdAt", set.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{setId}")
    public ResponseEntity<?> deleteSet(@PathVariable Integer setId,
                                        @RequestHeader("Authorization") String authHeader) {
        Integer userId = getUserIdFromToken(authHeader);
        Set set = setRepository.findById(setId)
                .orElse(null);

        if (set == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Set not found"));
        }

        if (!set.getCreatedBy().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Not authorized to delete this set"));
        }

        setRepository.delete(set);
        return ResponseEntity.ok(Map.of("message", "Set deleted successfully"));
    }
}
