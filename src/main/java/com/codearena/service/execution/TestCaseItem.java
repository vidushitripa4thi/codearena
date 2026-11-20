package com.codearena.service.execution;

public class TestCaseItem {
    private final Long id;
    private final String input;
    private final String expectedOutput;

    public TestCaseItem(Long id, String input, String expectedOutput) {
        this.id = id;
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    public Long getId() {
        return id;
    }

    public String getInput() {
        return input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }
}
