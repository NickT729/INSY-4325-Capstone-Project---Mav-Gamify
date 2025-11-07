package com.mavpal.repository;

import com.mavpal.entity.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetRepository extends JpaRepository<Set, Integer> {
    List<Set> findByCreatedBy(Integer createdBy);
    List<Set> findByVisibility(String visibility);
    
    @Query("SELECT s FROM Set s WHERE s.visibility = 'public' OR s.createdBy = :userId")
    List<Set> findPublicOrUserSets(@Param("userId") Integer userId);
    
    @Query("SELECT s FROM Set s WHERE (s.visibility = 'public' OR s.createdBy = :userId) AND (:subject IS NULL OR s.subject = :subject)")
    List<Set> findPublicOrUserSetsBySubject(@Param("userId") Integer userId, @Param("subject") String subject);
}
