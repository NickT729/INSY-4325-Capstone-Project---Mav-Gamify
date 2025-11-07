package com.mavpal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "daily_task_status", uniqueConstraints = @UniqueConstraint(columnNames = {"task_id", "date"}))
public class DailyTaskStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "task_id", nullable = false)
    private Integer taskId;

    @Column(nullable = false)
    private String date; // YYYY-MM-DD

    @Column(nullable = false)
    private Integer completed = 0;

    @Column(name = "completed_at")
    private String completedAt;

    // Constructors
    public DailyTaskStatus() {}

    public DailyTaskStatus(Integer taskId, String date, Integer completed, String completedAt) {
        this.taskId = taskId;
        this.date = date;
        this.completed = completed != null ? completed : 0;
        this.completedAt = completedAt;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}

