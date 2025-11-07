package com.mavpal.service;

import com.mavpal.entity.User;
import com.mavpal.entity.XpEvent;
import com.mavpal.repository.UserRepository;
import com.mavpal.repository.XpEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class XPService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private XpEventRepository xpEventRepository;

    private static final int DAILY_FULL_XP_LIMIT = 5; // First 5 sets per day give full XP
    private static final double REPEAT_XP_MULTIPLIER = 0.5; // Repeats give 50% XP

    public int calculateLevel(int xp) {
        return (int) Math.floor(Math.sqrt(xp / 100.0)) + 1;
    }

    @Transactional
    public int awardXp(Integer userId, int baseAmount, String eventType, Integer sourceSetId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check daily limits
        int actualAmount = calculateActualXpAmount(userId, baseAmount, eventType, sourceSetId);

        // Update user XP
        int newXp = user.getXp() + actualAmount;
        user.setXp(newXp);
        user.setLevel(calculateLevel(newXp));
        userRepository.save(user);

        // Create XP event
        XpEvent event = new XpEvent();
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setXpAmount(actualAmount);
        event.setSourceSet(sourceSetId);
        event.setCreatedAt(LocalDate.now().toString());
        xpEventRepository.save(event);

        return actualAmount;
    }

    /**
     * Create an XP event without awarding XP (for tracking purposes)
     * Used when we want to track an event but not award XP (e.g., quiz completion with < 100% score)
     */
    @Transactional
    public void createXpEvent(Integer userId, int xpAmount, String eventType, Integer sourceSetId) {
        XpEvent event = new XpEvent();
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setXpAmount(xpAmount);
        event.setSourceSet(sourceSetId);
        event.setCreatedAt(LocalDate.now().toString());
        xpEventRepository.save(event);
    }

    private int calculateActualXpAmount(Integer userId, int baseAmount, String eventType, Integer sourceSetId) {
        if (sourceSetId == null) {
            return baseAmount; // No daily cap for non-set events (like daily bonus)
        }

        // Count distinct sets completed today
        Long distinctSetsToday = xpEventRepository.countDistinctSetsToday(userId, eventType);
        
        if (distinctSetsToday < DAILY_FULL_XP_LIMIT) {
            // First N sets give full XP
            return baseAmount;
        } else {
            // Check if this specific set was already completed today
            Long eventsForSetToday = xpEventRepository.countEventsForSetToday(userId, sourceSetId);
            if (eventsForSetToday == 0) {
                // New set, but over daily limit - give reduced XP
                return (int) (baseAmount * REPEAT_XP_MULTIPLIER);
            } else {
                // Repeat of same set - give reduced XP
                return (int) (baseAmount * REPEAT_XP_MULTIPLIER);
            }
        }
    }

    public int getXpForQuizCompletion(double score) {
        // Only award XP if score is 100%
        if (score >= 100.0) {
            return 100;
        }
        return 0; // No XP for incomplete quizzes
    }

    public int getXpForFlashcardReview(int cardsReviewed) {
        // Fixed 100 XP for completing any flashcard review session
        return 100;
    }

    public int getXpForDailyBonus() {
        return 100; // Bonus for completing all daily tasks
    }
}
