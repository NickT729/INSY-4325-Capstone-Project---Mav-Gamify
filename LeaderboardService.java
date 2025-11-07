package com.mavpal.service;

import com.mavpal.entity.User;
import com.mavpal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> getTopUsers(int top, Integer currentUserId) {
        // Get top users by XP
        List<User> allUsers = userRepository.findAll(Sort.by(Sort.Direction.DESC, "xp"));
        List<User> topUsers = allUsers.stream().limit(top).collect(java.util.stream.Collectors.toList());

        List<Map<String, Object>> users = topUsers.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("utaId", user.getUtaId());
            map.put("firstName", user.getFirstName());
            map.put("lastName", user.getLastName());
            map.put("xp", user.getXp());
            map.put("level", user.getLevel());
            return map;
        }).collect(Collectors.toList());

        // Get current user's rank
        Long rank = null;
        if (currentUserId != null) {
            rank = userRepository.getUserRank(currentUserId);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("topUsers", users);
        if (rank != null) {
            response.put("userRank", rank);
        }

        return response;
    }

    public Long getUserRank(Integer userId) {
        return userRepository.getUserRank(userId);
    }
}
