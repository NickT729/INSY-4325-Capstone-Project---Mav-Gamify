package com.mavpal.repository;

import com.mavpal.entity.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Integer> {
    List<Flashcard> findBySetId(Integer setId);
    void deleteBySetId(Integer setId);
}
