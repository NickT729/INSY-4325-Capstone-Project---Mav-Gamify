package com.mavpal.service;

import com.mavpal.entity.DailyTask;
import com.mavpal.entity.DailyTaskStatus;
import com.mavpal.repository.DailyTaskRepository;
import com.mavpal.repository.DailyTaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChecklistService {

    @Autowired
    private DailyTaskRepository dailyTaskRepository;

    @Autowired
    private DailyTaskStatusRepository dailyTaskStatusRepository;

    @Autowired
    private XPService xpService;

    @Autowired
    private com.mavpal.repository.XpEventRepository xpEventRepository;

    public List<Map<String, Object>> getChecklistForDate(Integer userId, String date) {
        List<DailyTask> tasks = dailyTaskRepository.findByUserId(userId);
        List<Map<String, Object>> checklist = new ArrayList<>();

        for (DailyTask task : tasks) {
            // Filter out "Study for 30 minutes" task (deprecated)
            String taskTextLower = task.getTaskText().toLowerCase();
            if (taskTextLower.contains("30 minutes") || taskTextLower.contains("study for 30")) {
                continue; // Skip this task
            }
            
            DailyTaskStatus status = dailyTaskStatusRepository
                    .findByTaskIdAndDate(task.getId(), date)
                    .orElse(null);

            Map<String, Object> item = new HashMap<>();
            item.put("taskId", task.getId());
            item.put("taskText", task.getTaskText());
            item.put("isDefault", task.getIsDefault());
            item.put("completed", status != null && status.getCompleted() == 1);
            item.put("completedAt", status != null ? status.getCompletedAt() : null);
            checklist.add(item);
        }

        return checklist;
    }

    @Transactional
    public Map<String, Object> completeTask(Integer userId, Integer taskId, String date) {
        DailyTask task = dailyTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to complete this task");
        }

        DailyTaskStatus status = dailyTaskStatusRepository
                .findByTaskIdAndDate(taskId, date)
                .orElse(null);

        if (status == null) {
            status = new DailyTaskStatus();
            status.setTaskId(taskId);
            status.setDate(date);
        }

        status.setCompleted(1);
        status.setCompletedAt(LocalDateTime.now().toString());
        status = dailyTaskStatusRepository.save(status);

        // Check if all tasks are completed for today (excluding deprecated "Study for 30 minutes" task)
        List<DailyTask> allTasks = dailyTaskRepository.findByUserId(userId);
        boolean allCompleted = true;
        for (DailyTask t : allTasks) {
            // Skip "Study for 30 minutes" task when checking completion
            String taskTextLower = t.getTaskText().toLowerCase();
            if (taskTextLower.contains("30 minutes") || taskTextLower.contains("study for 30")) {
                continue; // Skip this task
            }
            
            DailyTaskStatus s = dailyTaskStatusRepository
                    .findByTaskIdAndDate(t.getId(), date)
                    .orElse(null);
            if (s == null || s.getCompleted() == 0) {
                allCompleted = false;
                break;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("completed", true);
        response.put("allCompleted", allCompleted);

        // Award daily bonus XP if all tasks completed
        if (allCompleted) {
            // Check if bonus was already awarded today by checking XP events
            List<com.mavpal.entity.XpEvent> todayEvents = xpEventRepository.findByUserId(userId);
            boolean bonusAwarded = todayEvents.stream()
                    .anyMatch(e -> e.getEventType().equals("daily_bonus") && 
                                 e.getCreatedAt() != null && 
                                 e.getCreatedAt().startsWith(date));
            
            if (!bonusAwarded) {
                int bonusXp = xpService.awardXp(userId, xpService.getXpForDailyBonus(), "daily_bonus", null);
                response.put("bonusXpAwarded", bonusXp);
            } else {
                response.put("bonusXpAwarded", 0); // Already awarded
            }
        }

        return response;
    }

    public void resetDailyChecklist(Integer userId) {
        // This would be called by a scheduled task or manually
        // For now, tasks reset automatically when checking a new date
    }
}
