package com.mavpal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "daily_tasks")
public class DailyTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "task_text", nullable = false)
    private String taskText;

    @Column(name = "is_default")
    private Integer isDefault = 0;

    // Constructors
    public DailyTask() {}

    public DailyTask(Integer userId, String taskText, Integer isDefault) {
        this.userId = userId;
        this.taskText = taskText;
        this.isDefault = isDefault != null ? isDefault : 0;
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

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }
}

