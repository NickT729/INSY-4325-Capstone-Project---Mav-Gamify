package com.mavpal.repository;

import com.mavpal.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
    List<QuizQuestion> findBySetId(Integer setId);
    void deleteBySetId(Integer setId);
}
