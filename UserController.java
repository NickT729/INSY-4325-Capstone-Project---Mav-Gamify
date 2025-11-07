package com.mavpal.controller;

import com.mavpal.entity.User;
import com.mavpal.entity.XpEvent;
import com.mavpal.entity.Set;
import com.mavpal.repository.UserRepository;
import com.mavpal.repository.XpEventRepository;
import com.mavpal.repository.SetRepository;
import com.mavpal.repository.DailyTaskRepository;
import com.mavpal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private XpEventRepository xpEventRepository;

    @Autowired
    private com.mavpal.service.XPService xpService;

    @Autowired
    private com.mavpal.service.DefaultSetService defaultSetService;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private DailyTaskRepository dailyTaskRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Integer getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }

    @GetMapping("/{utaId}")
    public ResponseEntity<?> getUser(@PathVariable String utaId) {
        User user = userRepository.findByUtaId(utaId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("utaId", user.getUtaId());
        userData.put("email", user.getEmail());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("xp", user.getXp());
        userData.put("level", user.getLevel());
        userData.put("createdAt", user.getCreatedAt());
        userData.put("lastLogin", user.getLastLogin());

        return ResponseEntity.ok(userData);
    }

    @GetMapping("/{utaId}/xp")
    public ResponseEntity<?> getXpHistory(@PathVariable String utaId,
                                         @RequestHeader("Authorization") String authHeader) {
        Integer userId = getUserIdFromToken(authHeader);
        User user = userRepository.findByUtaId(utaId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Not authorized"));
        }

        List<XpEvent> events = xpEventRepository.findByUserId(userId);
        List<Map<String, Object>> eventList = events.stream().map(event -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", event.getId());
            map.put("eventType", event.getEventType());
            map.put("xpAmount", event.getXpAmount());
            map.put("sourceSet", event.getSourceSet());
            map.put("createdAt", event.getCreatedAt());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("currentXp", user.getXp());
        response.put("currentLevel", user.getLevel());
        response.put("events", eventList);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{utaId}/xp/events")
    public ResponseEntity<?> createXpEvent(@PathVariable String utaId,
                                           @RequestBody Map<String, Object> request,
                                           @RequestHeader("Authorization") String authHeader) {
        Integer userId = getUserIdFromToken(authHeader);
        User user = userRepository.findByUtaId(utaId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Not authorized"));
        }

        String eventType = (String) request.get("eventType");
        Integer xpAmount = (Integer) request.get("xpAmount");
        Integer sourceSet = request.get("sourceSet") != null ? (Integer) request.get("sourceSet") : null;

        if (eventType == null || xpAmount == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing eventType or xpAmount"));
        }

        // Use XPService to award XP (handles level calculation and daily caps)
        int actualXp = xpService.awardXp(userId, xpAmount, eventType, sourceSet);

        // Get updated user
        user = userRepository.findById(userId).orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("xpEarned", actualXp);
        response.put("newXpTotal", user.getXp());
        response.put("level", user.getLevel());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{utaId}")
    public ResponseEntity<?> updateUser(@PathVariable String utaId,
                                        @RequestBody Map<String, Object> request,
                                        @RequestHeader("Authorization") String authHeader) {
        Integer userId = getUserIdFromToken(authHeader);
        User user = userRepository.findByUtaId(utaId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Not authorized"));
        }

        if (request.containsKey("firstName")) {
            user.setFirstName((String) request.get("firstName"));
        }
        if (request.containsKey("lastName")) {
            user.setLastName((String) request.get("lastName"));
        }

        user = userRepository.save(user);

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("utaId", user.getUtaId());
        userData.put("email", user.getEmail());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("xp", user.getXp());
        userData.put("level", user.getLevel());

        return ResponseEntity.ok(userData);
    }

    @PostMapping("/{utaId}/create-default-sets")
    public ResponseEntity<?> createDefaultSets(@PathVariable String utaId,
                                               @RequestHeader("Authorization") String authHeader) {
        Integer userId = getUserIdFromToken(authHeader);
        User user = userRepository.findByUtaId(utaId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Not authorized"));
        }

        // Check if default sets already exist
        List<Set> existingSets = setRepository.findByCreatedBy(userId);
        boolean hasDefaultSets = existingSets.stream()
                .anyMatch(s -> "Computer Science Fundamentals".equals(s.getTitle()) || 
                              "Computer Science Quiz".equals(s.getTitle()));
        
        if (hasDefaultSets) {
            return ResponseEntity.ok(Map.of("message", "Default sets already exist", "created", false));
        }

        // Create default sets
        defaultSetService.createDefaultSets(userId);

        return ResponseEntity.ok(Map.of("message", "Default sets created successfully", "created", true));
    }

    @DeleteMapping("/{utaId}/checklist/cleanup")
    public ResponseEntity<?> cleanupOldTasks(@PathVariable String utaId,
                                             @RequestHeader("Authorization") String authHeader) {
        Integer userId = getUserIdFromToken(authHeader);
        User user = userRepository.findByUtaId(utaId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Not authorized"));
        }

        // Delete "Study for 30 minutes" tasks for this user
        List<com.mavpal.entity.DailyTask> tasks = dailyTaskRepository.findByUserId(userId);
        int deletedCount = 0;
        for (com.mavpal.entity.DailyTask task : tasks) {
            String taskTextLower = task.getTaskText().toLowerCase();
            if (taskTextLower.contains("30 minutes") || taskTextLower.contains("study for 30")) {
                dailyTaskRepository.delete(task);
                deletedCount++;
            }
        }

        return ResponseEntity.ok(Map.of("message", "Old tasks cleaned up", "deletedCount", deletedCount));
    }
}
