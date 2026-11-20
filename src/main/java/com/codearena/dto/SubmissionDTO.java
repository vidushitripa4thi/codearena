package com.codearena.dto;

import com.codearena.entity.Language;
import com.codearena.entity.SubmissionStatus;
import com.codearena.service.execution.TestCaseResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

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
        private String compileError;
        private Integer totalTestCases;
        private Integer passedTestCases;
        private List<TestCaseResult> testCaseResults;

        public Response() {
        }

        public Response(Long id, Long problemId, String problemTitle, Long userId, String username,
                        Language language, SubmissionStatus status, String sourceCode,
                        Integer executionTime, Integer memoryUsed, Instant submittedAt) {
            this(id, problemId, problemTitle, userId, username, language, status, sourceCode,
                    executionTime, memoryUsed, submittedAt, null, null, null, null);
        }

        public Response(Long id, Long problemId, String problemTitle, Long userId, String username,
                        Language language, SubmissionStatus status, String sourceCode,
                        Integer executionTime, Integer memoryUsed, Instant submittedAt,
                        String compileError, Integer totalTestCases, Integer passedTestCases,
                        List<TestCaseResult> testCaseResults) {
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
            this.compileError = compileError;
            this.totalTestCases = totalTestCases;
            this.passedTestCases = passedTestCases;
            this.testCaseResults = testCaseResults;
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

        public String getCompileError() {
            return compileError;
        }

        public void setCompileError(String compileError) {
            this.compileError = compileError;
        }

        public Integer getTotalTestCases() {
            return totalTestCases;
        }

        public void setTotalTestCases(Integer totalTestCases) {
            this.totalTestCases = totalTestCases;
        }

        public Integer getPassedTestCases() {
            return passedTestCases;
        }

        public void setPassedTestCases(Integer passedTestCases) {
            this.passedTestCases = passedTestCases;
        }

        public List<TestCaseResult> getTestCaseResults() {
            return testCaseResults;
        }

        public void setTestCaseResults(List<TestCaseResult> testCaseResults) {
            this.testCaseResults = testCaseResults;
        }
    }

    public static class ExecutionResponse {
        private SubmissionStatus status;
        private String compileError;
        private Long executionTimeMs;
        private Integer totalTestCases;
        private Integer passedTestCases;
        private List<TestCaseResult> testCaseResults;

        public ExecutionResponse() {
        }

        public ExecutionResponse(SubmissionStatus status, String compileError, Long executionTimeMs,
                                 Integer totalTestCases, Integer passedTestCases, List<TestCaseResult> testCaseResults) {
            this.status = status;
            this.compileError = compileError;
            this.executionTimeMs = executionTimeMs;
            this.totalTestCases = totalTestCases;
            this.passedTestCases = passedTestCases;
            this.testCaseResults = testCaseResults;
        }

        public SubmissionStatus getStatus() {
            return status;
        }

        public void setStatus(SubmissionStatus status) {
            this.status = status;
        }

        public String getCompileError() {
            return compileError;
        }

        public void setCompileError(String compileError) {
            this.compileError = compileError;
        }

        public Long getExecutionTimeMs() {
            return executionTimeMs;
        }

        public void setExecutionTimeMs(Long executionTimeMs) {
            this.executionTimeMs = executionTimeMs;
        }

        public Integer getTotalTestCases() {
            return totalTestCases;
        }

        public void setTotalTestCases(Integer totalTestCases) {
            this.totalTestCases = totalTestCases;
        }

        public Integer getPassedTestCases() {
            return passedTestCases;
        }

        public void setPassedTestCases(Integer passedTestCases) {
            this.passedTestCases = passedTestCases;
        }

        public List<TestCaseResult> getTestCaseResults() {
            return testCaseResults;
        }

        public void setTestCaseResults(List<TestCaseResult> testCaseResults) {
            this.testCaseResults = testCaseResults;
        }
    }
}
