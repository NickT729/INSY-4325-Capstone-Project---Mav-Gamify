package com.mavpal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mavpal.entity.QuizQuestion;
import com.mavpal.entity.Set;
import com.mavpal.repository.QuizQuestionRepository;
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
@RequestMapping("/api/sets/{setId}")
@CrossOrigin(origins = "http://localhost:3000")
public class QuizController {

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Integer getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }

    @PostMapping("/questions")
    public ResponseEntity<?> addQuestion(@PathVariable Integer setId,
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
                    .body(Map.of("error", "Not authorized to add questions to this set"));
        }

        QuizQuestion question = new QuizQuestion();
        question.setSetId(setId);
        
        // Ensure type is set - default to "mcq" if not provided
        String questionType = (String) request.get("type");
        if (questionType == null || questionType.isEmpty()) {
            questionType = "mcq"; // Default to MCQ for quiz questions
        }
        question.setType(questionType);
        
        question.setQuestionText((String) request.get("questionText"));
        
        // For MCQ, ALWAYS store choices as JSON string (EXACT format as practice quiz)
        if ("mcq".equals(question.getType())) {
            if (request.get("choices") != null) {
                Object choicesObj = request.get("choices");
                String choicesJson = null;
                
                // ALWAYS use ObjectMapper first - it creates proper JSON
                try {
                    if (choicesObj instanceof List) {
                        // This creates: ["choice1","choice2","choice3"] - EXACT practice quiz format
                        // ObjectMapper.writeValueAsString will create proper JSON with escaped quotes
                        choicesJson = objectMapper.writeValueAsString(choicesObj);
                        System.out.println("ObjectMapper created JSON: " + choicesJson);
                    } else if (choicesObj instanceof String) {
                        // Already a string - validate and fix if needed
                        String str = ((String) choicesObj).trim();
                        if (str.startsWith("[") && str.endsWith("]")) {
                            // Try to parse to validate it's proper JSON
                            try {
                                List<?> parsed = objectMapper.readValue(str, List.class);
                                choicesJson = objectMapper.writeValueAsString(parsed);
                                System.out.println("Fixed string JSON: " + choicesJson);
                            } catch (Exception e) {
                                // Invalid JSON string - try to fix it
                                System.err.println("Invalid JSON string, attempting to fix: " + str);
                                // Remove brackets and split
                                String inner = str.substring(1, str.length() - 1).trim();
                                if (inner.length() > 0) {
                                    String[] parts = inner.split(",");
                                    List<String> fixedList = new java.util.ArrayList<>();
                                    for (String part : parts) {
                                        fixedList.add(part.trim().replaceAll("^[\"']|[\"']$", ""));
                                    }
                                    choicesJson = objectMapper.writeValueAsString(fixedList);
                                    System.out.println("Fixed invalid JSON to: " + choicesJson);
                                } else {
                                    choicesJson = "[]";
                                }
                            }
                        } else {
                            // Not JSON format, treat as single string and wrap in array
                            List<String> singleChoice = List.of(str);
                            choicesJson = objectMapper.writeValueAsString(singleChoice);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("ObjectMapper failed: " + e.getMessage());
                    e.printStackTrace();
                }
                
                // If ObjectMapper failed, create JSON manually (EXACT practice quiz format)
                if (choicesJson == null && choicesObj instanceof List) {
                    List<?> choices = (List<?>) choicesObj;
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < choices.size(); i++) {
                        if (i > 0) sb.append(",");
                        String choice = choices.get(i).toString();
                        // Escape quotes and wrap in quotes - EXACT format: "choice"
                        choice = choice.replace("\\", "\\\\").replace("\"", "\\\"");
                        sb.append("\"").append(choice).append("\"");
                    }
                    sb.append("]");
                    choicesJson = sb.toString();
                }
                
                if (choicesJson == null) {
                    choicesJson = "[]";
                }
                
                question.setChoices(choicesJson);
                System.out.println("DEBUG: Saved MCQ choices (format check): " + choicesJson);
            } else {
                System.err.println("ERROR: MCQ question created without choices!");
                question.setChoices("[]");
            }
        }
        
        if (request.get("correctIndex") != null) {
            question.setCorrectIndex((Integer) request.get("correctIndex"));
        }
        
        question.setHint((String) request.get("hint"));
        question.setOrder((Integer) request.getOrDefault("order", 0));

        question = quizQuestionRepository.save(question);

        Map<String, Object> response = new HashMap<>();
        response.put("id", question.getId());
        response.put("setId", question.getSetId());
        response.put("type", question.getType());
        response.put("questionText", question.getQuestionText());
        response.put("choices", question.getChoices());
        response.put("correctIndex", question.getCorrectIndex());
        response.put("hint", question.getHint());
        response.put("order", question.getOrder());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(@PathVariable Integer setId) {
        Set set = setRepository.findById(setId)
                .orElse(null);

        if (set == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Set not found"));
        }

        List<QuizQuestion> questions = quizQuestionRepository.findBySetId(setId);
        List<Map<String, Object>> response = questions.stream().map(q -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", q.getId());
            map.put("setId", q.getSetId());
            map.put("type", q.getType() != null ? q.getType() : "mcq"); // Default to mcq if null
            map.put("questionText", q.getQuestionText());
            // ALWAYS include choices field - even if null, return empty string
            map.put("choices", q.getChoices() != null ? q.getChoices() : "[]");
            map.put("correctIndex", q.getCorrectIndex());
            map.put("hint", q.getHint());
            map.put("order", q.getOrder());
            
            // Debug logging
            System.out.println("DEBUG GET: Question " + q.getId() + " - type: " + q.getType() + ", choices: " + q.getChoices());
            
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/questions/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Integer setId,
                                             @PathVariable Integer questionId,
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
                    .body(Map.of("error", "Not authorized to update questions in this set"));
        }

        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .orElse(null);

        if (question == null || !question.getSetId().equals(setId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Question not found"));
        }

        if (request.containsKey("questionText")) {
            question.setQuestionText((String) request.get("questionText"));
        }
        if (request.containsKey("choices")) {
            List<?> choices = (List<?>) request.get("choices");
            question.setChoices(choices.toString());
        }
        if (request.containsKey("correctIndex")) {
            question.setCorrectIndex((Integer) request.get("correctIndex"));
        }
        if (request.containsKey("hint")) {
            question.setHint((String) request.get("hint"));
        }
        if (request.containsKey("order")) {
            question.setOrder((Integer) request.get("order"));
        }

        question = quizQuestionRepository.save(question);

        Map<String, Object> response = new HashMap<>();
        response.put("id", question.getId());
        response.put("setId", question.getSetId());
        response.put("type", question.getType());
        response.put("questionText", question.getQuestionText());
        response.put("choices", question.getChoices());
        response.put("correctIndex", question.getCorrectIndex());
        response.put("hint", question.getHint());
        response.put("order", question.getOrder());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Integer setId,
                                             @PathVariable Integer questionId,
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
                    .body(Map.of("error", "Not authorized to delete questions from this set"));
        }

        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .orElse(null);

        if (question == null || !question.getSetId().equals(setId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Question not found"));
        }

        quizQuestionRepository.delete(question);
        return ResponseEntity.ok(Map.of("message", "Question deleted successfully"));
    }
}
