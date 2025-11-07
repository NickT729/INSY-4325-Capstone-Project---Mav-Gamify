package com.mavpal.repository;

import com.mavpal.entity.DailyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyTaskRepository extends JpaRepository<DailyTask, Integer> {
    List<DailyTask> findByUserId(Integer userId);
}
