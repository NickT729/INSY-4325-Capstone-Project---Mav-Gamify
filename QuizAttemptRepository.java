package com.mavpal.repository;

import com.mavpal.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Integer> {
    List<QuizAttempt> findByUserId(Integer userId);
    List<QuizAttempt> findBySetId(Integer setId);
    List<QuizAttempt> findByUserIdAndSetId(Integer userId, Integer setId);
}
