package com.codearena.dto;

import jakarta.validation.constraints.NotBlank;

public class ExampleDTO {

    private Long id;

    @NotBlank(message = "Example input is required")
    private String input;

    @NotBlank(message = "Example output is required")
    private String output;

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

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
