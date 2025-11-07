package com.mavpal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "set_id", nullable = false)
    private Integer setId;

    @Column(nullable = false)
    private String type; // "mcq" or "short"

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String choices; // JSON array string for MCQ

    @Column(name = "correct_index")
    private Integer correctIndex;

    @Column(columnDefinition = "TEXT")
    private String hint;

    @Column(name = "ord")
    private Integer order = 0;

    // Constructors
    public QuizQuestion() {}

    public QuizQuestion(Integer setId, String type, String questionText, String choices, Integer correctIndex, String hint, Integer order) {
        this.setId = setId;
        this.type = type;
        this.questionText = questionText;
        this.choices = choices;
        this.correctIndex = correctIndex;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }

    public Integer getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(Integer correctIndex) {
        this.correctIndex = correctIndex;
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

