package com.mavpal.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mavpal.entity.QuizAttempt;
import com.mavpal.entity.QuizQuestion;
import com.mavpal.entity.Set;
import com.mavpal.repository.QuizAttemptRepository;
import com.mavpal.repository.QuizQuestionRepository;
import com.mavpal.repository.SetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private XPService xpService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Map<String, Object> gradeQuiz(Integer setId, Integer userId, List<Map<String, Object>> answers, Integer durationMs) {
        Set set = setRepository.findById(setId)
                .orElseThrow(() -> new RuntimeException("Set not found"));

        List<QuizQuestion> questions = quizQuestionRepository.findBySetId(setId);
        if (questions.isEmpty()) {
            throw new RuntimeException("No questions found for this set");
        }

        int correct = 0;
        int total = questions.size();
        List<Map<String, Object>> results = new ArrayList<>();

        for (QuizQuestion question : questions) {
            Map<String, Object> result = new HashMap<>();
            result.put("questionId", question.getId());
            result.put("correct", false);

            // Find user's answer for this question
            Map<String, Object> userAnswer = answers.stream()
                    .filter(a -> question.getId().equals(a.get("questionId")))
                    .findFirst()
                    .orElse(null);

            if (userAnswer != null) {
                boolean isCorrect = false;

                if ("mcq".equals(question.getType())) {
                    // Multiple choice: check if selected index matches correct index
                    Integer selectedIndex = (Integer) userAnswer.get("answerOrChoiceIndex");
                    if (selectedIndex != null && selectedIndex.equals(question.getCorrectIndex())) {
                        isCorrect = true;
                        correct++;
                    }
                } else if ("short".equals(question.getType())) {
                    // Short answer: case-insensitive, trimmed comparison
                    String userAnswerText = ((String) userAnswer.get("answerOrChoiceIndex")).trim().toLowerCase();
                    // For short answer, we'd need to store the correct answer text
                    // For now, we'll use a simple approach - this would need enhancement
                    // This is a limitation - short answer questions need correct answer text stored
                }

                result.put("correct", isCorrect);
                result.put("userAnswer", userAnswer.get("answerOrChoiceIndex"));
            }

            results.add(result);
        }

        // Calculate score
        double score = (correct * 100.0) / total;

        // Calculate XP earned - only award if score is 100%
        int baseXp = xpService.getXpForQuizCompletion(score);
        int xpEarned = 0;
        if (baseXp > 0) {
            // Only award XP if score is 100%
            xpEarned = xpService.awardXp(userId, baseXp, "quiz_complete", setId);
        } else {
            // Still create XP event (with 0 XP) so checklist can detect quiz completion
            // This allows the checklist to update even if score < 100%
            xpService.createXpEvent(userId, 0, "quiz_complete", setId);
        }

        // Get updated user info
        // This would require fetching user, but for now we'll return what we can

        // Create quiz attempt record
        QuizAttempt attempt = new QuizAttempt();
        attempt.setSetId(setId);
        attempt.setUserId(userId);
        attempt.setScore(score);
        attempt.setXpEarned(xpEarned);
        attempt.setStartedAt(LocalDateTime.now().minusSeconds(durationMs / 1000).toString());
        attempt.setCompletedAt(LocalDateTime.now().toString());
        attempt.setDurationMs(durationMs);
        
        try {
            attempt.setDetails(objectMapper.writeValueAsString(results));
        } catch (Exception e) {
            attempt.setDetails("[]");
        }

        attempt = quizAttemptRepository.save(attempt);

        Map<String, Object> response = new HashMap<>();
        response.put("score", score);
        response.put("correct", correct);
        response.put("total", total);
        response.put("xpEarned", xpEarned);
        response.put("attemptId", attempt.getId());
        response.put("results", results);

        return response;
    }
}
