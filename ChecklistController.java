package com.mavpal.controller;

import com.mavpal.service.ChecklistService;
import com.mavpal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/users/{utaId}/checklist")
@CrossOrigin(origins = "http://localhost:3000")
public class ChecklistController {

    @Autowired
    private ChecklistService checklistService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.mavpal.repository.UserRepository userRepository;

    private Integer getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }

    @GetMapping
    public ResponseEntity<?> getChecklist(@PathVariable String utaId,
                                          @RequestParam(required = false) String date,
                                          @RequestHeader("Authorization") String authHeader) {
        Integer userId = getUserIdFromToken(authHeader);
        
        // Verify user matches
        var user = userRepository.findByUtaId(utaId).orElse(null);
        if (user == null || !user.getId().equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Not authorized"));
        }

        String checkDate = date != null ? date : LocalDate.now().toString();
        var checklist = checklistService.getChecklistForDate(userId, checkDate);
        return ResponseEntity.ok(checklist);
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<?> completeTask(@PathVariable String utaId,
                                          @PathVariable Integer taskId,
                                          @RequestParam(required = false) String date,
                                          @RequestHeader("Authorization") String authHeader) {
        Integer userId = getUserIdFromToken(authHeader);
        
        // Verify user matches
        var user = userRepository.findByUtaId(utaId).orElse(null);
        if (user == null || !user.getId().equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Not authorized"));
        }

        String checkDate = date != null ? date : LocalDate.now().toString();
        var result = checklistService.completeTask(userId, taskId, checkDate);
        return ResponseEntity.ok(result);
    }
}
