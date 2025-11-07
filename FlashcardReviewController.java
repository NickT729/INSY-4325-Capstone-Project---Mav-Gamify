package com.mavpal.controller;

import com.mavpal.service.XPService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sets/{setId}/review")
@CrossOrigin(origins = "http://localhost:3000")
public class FlashcardReviewController {

    @Autowired
    private XPService xpService;

    @PostMapping
    public ResponseEntity<?> completeReview(
            @PathVariable Long setId,
            @RequestParam(defaultValue = "10") int cardsReviewed,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Award XP for flashcard review (base 5 XP per card, max 50 XP per session)
        int baseXP = Math.min(cardsReviewed * 5, 50);
        int xpEarned = xpService.awardXp(userId.intValue(), baseXP, "flashcard_review", setId.intValue());

        ReviewResponse response = new ReviewResponse();
        response.setXpEarned(xpEarned);
        response.setCardsReviewed(cardsReviewed);

        return ResponseEntity.ok(response);
    }

    private static class ReviewResponse {
        private Integer xpEarned;
        private Integer cardsReviewed;

        public Integer getXpEarned() {
            return xpEarned;
        }

        public void setXpEarned(Integer xpEarned) {
            this.xpEarned = xpEarned;
        }

        public Integer getCardsReviewed() {
            return cardsReviewed;
        }

        public void setCardsReviewed(Integer cardsReviewed) {
            this.cardsReviewed = cardsReviewed;
        }
    }
}

