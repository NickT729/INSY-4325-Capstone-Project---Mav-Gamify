package com.mavpal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "xp_events")
public class XpEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "xp_amount", nullable = false)
    private Integer xpAmount;

    @Column(name = "source_set")
    private Integer sourceSet;

    @Column(name = "created_at")
    private String createdAt;

    // Constructors
    public XpEvent() {}

    public XpEvent(Integer userId, String eventType, Integer xpAmount, Integer sourceSet) {
        this.userId = userId;
        this.eventType = eventType;
        this.xpAmount = xpAmount;
        this.sourceSet = sourceSet;
        this.createdAt = java.time.LocalDateTime.now().toString();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getXpAmount() {
        return xpAmount;
    }

    public void setXpAmount(Integer xpAmount) {
        this.xpAmount = xpAmount;
    }

    public Integer getSourceSet() {
        return sourceSet;
    }

    public void setSourceSet(Integer sourceSet) {
        this.sourceSet = sourceSet;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

