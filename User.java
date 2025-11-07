package com.mavpal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uta_id", unique = true, nullable = false, length = 10)
    private String utaId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Integer xp = 0;

    @Column(nullable = false)
    private Integer level = 1;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "last_login")
    private String lastLogin;

    // Constructors
    public User() {}

    public User(String utaId, String email, String firstName, String lastName, String passwordHash) {
        this.utaId = utaId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHash = passwordHash;
        this.xp = 0;
        this.level = 1;
        this.createdAt = LocalDateTime.now().toString();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUtaId() {
        return utaId;
    }

    public void setUtaId(String utaId) {
        this.utaId = utaId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }
}

