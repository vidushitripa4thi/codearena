package com.codearena.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class TestCaseDTO {

    private Long id;

    @NotBlank(message = "Test case input is required")
    private String input;

    @NotBlank(message = "Expected output is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String expectedOutput;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
