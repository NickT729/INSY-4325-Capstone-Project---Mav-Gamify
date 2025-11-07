package com.mavpal.repository;

import com.mavpal.entity.XpEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XpEventRepository extends JpaRepository<XpEvent, Integer> {
    List<XpEvent> findByUserId(Integer userId);
    
    @Query(value = "SELECT COUNT(DISTINCT source_set) FROM xp_events WHERE user_id = :userId AND event_type = :eventType AND date(created_at) = date('now')", nativeQuery = true)
    Long countDistinctSetsToday(@Param("userId") Integer userId, @Param("eventType") String eventType);
    
    @Query(value = "SELECT COUNT(*) FROM xp_events WHERE user_id = :userId AND source_set = :setId AND date(created_at) = date('now')", nativeQuery = true)
    Long countEventsForSetToday(@Param("userId") Integer userId, @Param("setId") Integer setId);
}
