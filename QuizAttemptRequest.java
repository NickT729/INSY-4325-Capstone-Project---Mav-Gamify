package com.mavpal.dto;

import java.util.List;

public class QuizAttemptRequest {
    private List<AnswerSubmission> answers;
    private Long durationMs;

    public static class AnswerSubmission {
        private Long questionId;
        private Object answer; // Can be Integer (for MCQ) or String (for short answer)

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public Object getAnswer() {
            return answer;
        }

        public void setAnswer(Object answer) {
            this.answer = answer;
        }
    }

    public List<AnswerSubmission> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerSubmission> answers) {
        this.answers = answers;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
}

