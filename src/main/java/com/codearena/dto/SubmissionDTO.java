package com.codearena.dto;

import com.codearena.entity.Language;
import com.codearena.entity.SubmissionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class SubmissionDTO {

    public static class CreateRequest {
        @NotNull(message = "Problem ID is required")
        private Long problemId;

        @NotBlank(message = "Source code is required")
        @Size(max = 65535, message = "Source code cannot exceed 65535 characters")
        private String sourceCode;

        @NotNull(message = "Language is required")
        private Language language;

        public Long getProblemId() {
            return problemId;
        }

        public void setProblemId(Long problemId) {
            this.problemId = problemId;
        }

        public String getSourceCode() {
            return sourceCode;
        }

        public void setSourceCode(String sourceCode) {
            this.sourceCode = sourceCode;
        }

        public Language getLanguage() {
            return language;
        }

        public void setLanguage(Language language) {
            this.language = language;
        }
    }

    public static class Response {
        private Long id;
        private Long problemId;
        private String problemTitle;
        private Long userId;
        private String username;
        private Language language;
        private SubmissionStatus status;
        private String sourceCode;
        private Integer executionTime;
        private Integer memoryUsed;
        private Instant submittedAt;

        public Response() {
        }

        public Response(Long id, Long problemId, String problemTitle, Long userId, String username,
                        Language language, SubmissionStatus status, String sourceCode,
                        Integer executionTime, Integer memoryUsed, Instant submittedAt) {
            this.id = id;
            this.problemId = problemId;
            this.problemTitle = problemTitle;
            this.userId = userId;
            this.username = username;
            this.language = language;
            this.status = status;
            this.sourceCode = sourceCode;
            this.executionTime = executionTime;
            this.memoryUsed = memoryUsed;
            this.submittedAt = submittedAt;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getProblemId() {
            return problemId;
        }

        public void setProblemId(Long problemId) {
            this.problemId = problemId;
        }

        public String getProblemTitle() {
            return problemTitle;
        }

        public void setProblemTitle(String problemTitle) {
            this.problemTitle = problemTitle;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Language getLanguage() {
            return language;
        }

        public void setLanguage(Language language) {
            this.language = language;
        }

        public SubmissionStatus getStatus() {
            return status;
        }

        public void setStatus(SubmissionStatus status) {
            this.status = status;
        }

        public String getSourceCode() {
            return sourceCode;
        }

        public void setSourceCode(String sourceCode) {
            this.sourceCode = sourceCode;
        }

        public Integer getExecutionTime() {
            return executionTime;
        }

        public void setExecutionTime(Integer executionTime) {
            this.executionTime = executionTime;
        }

        public Integer getMemoryUsed() {
            return memoryUsed;
        }

        public void setMemoryUsed(Integer memoryUsed) {
            this.memoryUsed = memoryUsed;
        }

        public Instant getSubmittedAt() {
            return submittedAt;
        }

        public void setSubmittedAt(Instant submittedAt) {
            this.submittedAt = submittedAt;
        }
    }
}
