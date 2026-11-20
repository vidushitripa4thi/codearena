package com.codearena.service.execution;

import com.codearena.entity.SubmissionStatus;

import java.util.ArrayList;
import java.util.List;

public class ExecutionResult {
    private SubmissionStatus status;
    private String compileError;
    private Long executionTimeMs = 0L;
    private Long memoryUsedKb = 0L;
    private int totalTestCases = 0;
    private int passedTestCases = 0;
    private List<TestCaseResult> testCaseResults = new ArrayList<>();

    public ExecutionResult() {
    }

    public ExecutionResult(SubmissionStatus status, String compileError) {
        this.status = status;
        this.compileError = compileError;
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

    public Long getMemoryUsedKb() {
        return memoryUsedKb;
    }

    public void setMemoryUsedKb(Long memoryUsedKb) {
        this.memoryUsedKb = memoryUsedKb;
    }

    public int getTotalTestCases() {
        return totalTestCases;
    }

    public void setTotalTestCases(int totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    public int getPassedTestCases() {
        return passedTestCases;
    }

    public void setPassedTestCases(int passedTestCases) {
        this.passedTestCases = passedTestCases;
    }

    public List<TestCaseResult> getTestCaseResults() {
        return testCaseResults;
    }

    public void setTestCaseResults(List<TestCaseResult> testCaseResults) {
        this.testCaseResults = testCaseResults;
    }

    public void addTestCaseResult(TestCaseResult result) {
        this.testCaseResults.add(result);
        this.totalTestCases++;
        if (result.isPassed()) {
            this.passedTestCases++;
        }
    }
}
