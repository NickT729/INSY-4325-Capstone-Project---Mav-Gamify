package com.mavpal.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordValidationService {

    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 10) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public int calculatePasswordStrength(String password) {
        if (password == null) return 0;
        
        int strength = 0;
        if (password.length() >= 10) strength += 1;
        if (password.length() >= 12) strength += 1;
        if (password.length() >= 16) strength += 1;
        
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        
        if (hasUpper) strength += 1;
        if (hasLower) strength += 1;
        if (hasDigit) strength += 1;
        if (hasSpecial) strength += 1;
        
        return Math.min(strength, 5); // Max score of 5
    }
}
