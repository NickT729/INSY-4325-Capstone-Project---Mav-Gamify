package com.mavpal.repository;

import com.mavpal.entity.DailyTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyTaskStatusRepository extends JpaRepository<DailyTaskStatus, Integer> {
    Optional<DailyTaskStatus> findByTaskIdAndDate(Integer taskId, String date);
    List<DailyTaskStatus> findByTaskId(Integer taskId);
    List<DailyTaskStatus> findByDate(String date);
}
