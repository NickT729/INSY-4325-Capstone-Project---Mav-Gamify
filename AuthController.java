package com.mavpal.controller;

import com.mavpal.dto.LoginRequest;
import com.mavpal.dto.RegisterRequest;
import com.mavpal.entity.DailyTask;
import com.mavpal.entity.User;
import com.mavpal.repository.DailyTaskRepository;
import com.mavpal.repository.UserRepository;
import com.mavpal.service.PasswordValidationService;
import com.mavpal.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordValidationService passwordValidationService;

    @Autowired
    private DailyTaskRepository dailyTaskRepository;

    @Autowired
    private com.mavpal.service.DefaultSetService defaultSetService;

    @Autowired
    private com.mavpal.repository.SetRepository setRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // Validate email domain
        if (!request.getEmail().endsWith("@mavs.uta.edu")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email must end with @mavs.uta.edu"));
        }

        // Validate password match
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Passwords do not match"));
        }

        // Validate password strength
        if (!passwordValidationService.isValidPassword(request.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password must be at least 10 characters with uppercase, lowercase, digit, and special character"));
        }

        // Check if UTA ID already exists
        if (userRepository.existsByUtaId(request.getUtaId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "UTA ID already registered"));
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already registered"));
        }

        // Create user
        User user = new User();
        user.setUtaId(request.getUtaId());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setXp(0);
        user.setLevel(1);
        user.setCreatedAt(LocalDateTime.now().toString());

        user = userRepository.save(user);

        // Create default daily tasks (only 4 tasks for daily bonus)
        String[] defaultTasks = {
            "Review at least 1 flashcard set",
            "Complete at least 1 quiz",
            "Create or edit a study set",
            "Check the leaderboard"
        };

        for (String taskText : defaultTasks) {
            DailyTask task = new DailyTask();
            task.setUserId(user.getId());
            task.setTaskText(taskText);
            task.setIsDefault(1);
            dailyTaskRepository.save(task);
        }

        // Create default Computer Science sets
        defaultSetService.createDefaultSets(user.getId());

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUtaId());

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("token", token);
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("utaId", user.getUtaId());
        userData.put("email", user.getEmail());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("xp", user.getXp());
        userData.put("level", user.getLevel());
        response.put("user", userData);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now().toString());
        userRepository.save(user);

        // Create default sets if user doesn't have any
        long setCount = setRepository.findByCreatedBy(user.getId()).size();
        if (setCount == 0) {
            defaultSetService.createDefaultSets(user.getId());
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUtaId());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("utaId", user.getUtaId());
        userData.put("email", user.getEmail());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("xp", user.getXp());
        userData.put("level", user.getLevel());
        response.put("user", userData);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Integer userId = jwtUtil.extractUserId(token);

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
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

        return ResponseEntity.ok(Map.of("user", userData));
    }
}
