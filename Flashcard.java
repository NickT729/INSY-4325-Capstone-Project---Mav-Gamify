package com.mavpal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "flashcards")
public class Flashcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "set_id", nullable = false)
    private Integer setId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(columnDefinition = "TEXT")
    private String hint;

    @Column(name = "ord")
    private Integer order = 0;

    // Constructors
    public Flashcard() {}

    public Flashcard(Integer setId, String question, String answer, String hint, Integer order) {
        this.setId = setId;
        this.question = question;
        this.answer = answer;
        this.hint = hint;
        this.order = order != null ? order : 0;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}

