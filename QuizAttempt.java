package com.mavpal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "set_id", nullable = false)
    private Integer setId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    private Double score;

    @Column(name = "xp_earned")
    private Integer xpEarned;

    @Column(name = "started_at")
    private String startedAt;

    @Column(name = "completed_at")
    private String completedAt;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(columnDefinition = "TEXT")
    private String details; // JSON summary of answers

    // Constructors
    public QuizAttempt() {}

    public QuizAttempt(Integer setId, Integer userId, Double score, Integer xpEarned, String startedAt, String completedAt, Integer durationMs, String details) {
        this.setId = setId;
        this.userId = userId;
        this.score = score;
        this.xpEarned = xpEarned;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.durationMs = durationMs;
        this.details = details;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Integer durationMs) {
        this.durationMs = durationMs;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

