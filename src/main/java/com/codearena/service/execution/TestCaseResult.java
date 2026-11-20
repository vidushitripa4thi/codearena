package com.codearena.service.execution;

import com.codearena.entity.SubmissionStatus;

public class TestCaseResult {
    private int caseNumber;
    private String input;
    private String expectedOutput;
    private String actualOutput;
    private boolean passed;
    private SubmissionStatus status;
    private Long executionTimeMs;
    private String errorMessage;

    public TestCaseResult() {
    }

    public TestCaseResult(int caseNumber, String input, String expectedOutput, String actualOutput,
                          boolean passed, SubmissionStatus status, Long executionTimeMs, String errorMessage) {
        this.caseNumber = caseNumber;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.actualOutput = actualOutput;
        this.passed = passed;
        this.status = status;
        this.executionTimeMs = executionTimeMs;
        this.errorMessage = errorMessage;
    }

    public int getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(int caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public String getActualOutput() {
        return actualOutput;
    }

    public void setActualOutput(String actualOutput) {
        this.actualOutput = actualOutput;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
