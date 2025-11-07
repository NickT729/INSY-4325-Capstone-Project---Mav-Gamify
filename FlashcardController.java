package com.mavpal.controller;

import com.mavpal.entity.Flashcard;
import com.mavpal.entity.Set;
import com.mavpal.repository.FlashcardRepository;
import com.mavpal.repository.SetRepository;
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
@RequestMapping("/api/sets/{setId}/flashcards")
@CrossOrigin(origins = "http://localhost:3000")
public class FlashcardController {

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Integer getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }

    @PostMapping
    public ResponseEntity<?> addFlashcard(@PathVariable Integer setId,
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
                    .body(Map.of("error", "Not authorized to add flashcards to this set"));
        }

        Flashcard flashcard = new Flashcard();
        flashcard.setSetId(setId);
        flashcard.setQuestion((String) request.get("question"));
        flashcard.setAnswer((String) request.get("answer"));
        flashcard.setHint((String) request.get("hint"));
        flashcard.setOrder((Integer) request.getOrDefault("order", 0));

        flashcard = flashcardRepository.save(flashcard);

        Map<String, Object> response = new HashMap<>();
        response.put("id", flashcard.getId());
        response.put("setId", flashcard.getSetId());
        response.put("question", flashcard.getQuestion());
        response.put("answer", flashcard.getAnswer());
        response.put("hint", flashcard.getHint());
        response.put("order", flashcard.getOrder());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getFlashcards(@PathVariable Integer setId) {
        Set set = setRepository.findById(setId)
                .orElse(null);

        if (set == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Set not found"));
        }

        List<Flashcard> flashcards = flashcardRepository.findBySetId(setId);
        List<Map<String, Object>> response = flashcards.stream().map(card -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", card.getId());
            map.put("setId", card.getSetId());
            map.put("question", card.getQuestion());
            map.put("answer", card.getAnswer());
            map.put("hint", card.getHint());
            map.put("order", card.getOrder());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{cardId}")
    public ResponseEntity<?> updateFlashcard(@PathVariable Integer setId,
                                              @PathVariable Integer cardId,
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
                    .body(Map.of("error", "Not authorized to update flashcards in this set"));
        }

        Flashcard flashcard = flashcardRepository.findById(cardId)
                .orElse(null);

        if (flashcard == null || !flashcard.getSetId().equals(setId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Flashcard not found"));
        }

        if (request.containsKey("question")) {
            flashcard.setQuestion((String) request.get("question"));
        }
        if (request.containsKey("answer")) {
            flashcard.setAnswer((String) request.get("answer"));
        }
        if (request.containsKey("hint")) {
            flashcard.setHint((String) request.get("hint"));
        }
        if (request.containsKey("order")) {
            flashcard.setOrder((Integer) request.get("order"));
        }

        flashcard = flashcardRepository.save(flashcard);

        Map<String, Object> response = new HashMap<>();
        response.put("id", flashcard.getId());
        response.put("setId", flashcard.getSetId());
        response.put("question", flashcard.getQuestion());
        response.put("answer", flashcard.getAnswer());
        response.put("hint", flashcard.getHint());
        response.put("order", flashcard.getOrder());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteFlashcard(@PathVariable Integer setId,
                                              @PathVariable Integer cardId,
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
                    .body(Map.of("error", "Not authorized to delete flashcards from this set"));
        }

        Flashcard flashcard = flashcardRepository.findById(cardId)
                .orElse(null);

        if (flashcard == null || !flashcard.getSetId().equals(setId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Flashcard not found"));
        }

        flashcardRepository.delete(flashcard);
        return ResponseEntity.ok(Map.of("message", "Flashcard deleted successfully"));
    }
}
