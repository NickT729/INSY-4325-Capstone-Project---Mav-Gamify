package com.mavpal.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "sets")
public class Set {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String subject;

    @Column(nullable = false)
    private String visibility = "private";

    @Column(nullable = false)
    private String type; // "flashcard" or "quiz"

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "created_at")
    private String createdAt;

    // Constructors
    public Set() {}

    public Set(String title, String description, String subject, String visibility, String type, Integer createdBy) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.visibility = visibility != null ? visibility : "private";
        this.type = type;
        this.createdBy = createdBy;
        this.createdAt = java.time.LocalDateTime.now().toString();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

