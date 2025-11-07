package com.mavpal.dto;

public class QuizAttemptResponse {
    private Long attemptId;
    private Double score;
    private Integer xpEarned;
    private Integer newXpTotal;
    private Integer level;

    public QuizAttemptResponse() {}

    public QuizAttemptResponse(Long attemptId, Double score, Integer xpEarned, Integer newXpTotal, Integer level) {
        this.attemptId = attemptId;
        this.score = score;
        this.xpEarned = xpEarned;
        this.newXpTotal = newXpTotal;
        this.level = level;
    }

    // Getters and Setters
    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(Integer xpEarned) {
        this.xpEarned = xpEarned;
    }

    public Integer getNewXpTotal() {
        return newXpTotal;
    }

    public void setNewXpTotal(Integer newXpTotal) {
        this.newXpTotal = newXpTotal;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}

