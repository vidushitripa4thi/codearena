package com.codearena.service;

import com.codearena.dto.ExampleDTO;
import com.codearena.dto.ProblemDTO;
import com.codearena.dto.TestCaseDTO;
import com.codearena.entity.Example;
import com.codearena.entity.Problem;
import com.codearena.entity.TestCase;
import org.springframework.stereotype.Component;

@Component
public class ProblemMapper {

    public Problem toEntity(ProblemDTO dto) {
        Problem problem = new Problem();
        if (dto.getCreatedAt() != null) {
            problem.setCreatedAt(dto.getCreatedAt());
        }
        updateEntity(problem, dto);
        return problem;
    }

    public void updateEntity(Problem problem, ProblemDTO dto) {
        problem.setTitle(dto.getTitle());
        problem.setDescription(dto.getDescription());
        problem.setDifficulty(dto.getDifficulty());
        problem.setConstraints(dto.getConstraints());
        problem.setInputFormat(dto.getInputFormat());
        problem.setOutputFormat(dto.getOutputFormat());
        problem.setTimeLimit(dto.getTimeLimit());
        problem.setMemoryLimit(dto.getMemoryLimit());
        problem.setExamples(dto.getExamples().stream().map(this::toEntity).toList());
        problem.setTestCases(dto.getTestCases().stream().map(this::toEntity).toList());
    }

    public ProblemDTO toDto(Problem problem) {
        ProblemDTO dto = new ProblemDTO();
        dto.setId(problem.getId());
        dto.setTitle(problem.getTitle());
        dto.setDescription(problem.getDescription());
        dto.setDifficulty(problem.getDifficulty());
        dto.setConstraints(problem.getConstraints());
        dto.setInputFormat(problem.getInputFormat());
        dto.setOutputFormat(problem.getOutputFormat());
        dto.setTimeLimit(problem.getTimeLimit());
        dto.setMemoryLimit(problem.getMemoryLimit());
        dto.setCreatedAt(problem.getCreatedAt());
        dto.setExamples(problem.getExamples().stream().map(this::toDto).toList());
        dto.setTestCases(problem.getTestCases().stream().map(this::toDto).toList());
        return dto;
    }

    public ProblemDTO toPublicDto(Problem problem) {
        ProblemDTO dto = new ProblemDTO();
        dto.setId(problem.getId());
        dto.setTitle(problem.getTitle());
        dto.setDescription(problem.getDescription());
        dto.setDifficulty(problem.getDifficulty());
        dto.setConstraints(problem.getConstraints());
        dto.setInputFormat(problem.getInputFormat());
        dto.setOutputFormat(problem.getOutputFormat());
        dto.setTimeLimit(problem.getTimeLimit());
        dto.setMemoryLimit(problem.getMemoryLimit());
        dto.setCreatedAt(problem.getCreatedAt());
        dto.setExamples(problem.getExamples().stream().map(this::toDto).toList());
        dto.setTestCases(java.util.Collections.emptyList());
        return dto;
    }

    private Example toEntity(ExampleDTO dto) {
        Example example = new Example();
        example.setInput(dto.getInput());
        example.setOutput(dto.getOutput());
        return example;
    }

    private TestCase toEntity(TestCaseDTO dto) {
        TestCase testCase = new TestCase();
        testCase.setInput(dto.getInput());
        testCase.setExpectedOutput(dto.getExpectedOutput());
        return testCase;
    }

    private ExampleDTO toDto(Example example) {
        ExampleDTO dto = new ExampleDTO();
        dto.setId(example.getId());
        dto.setInput(example.getInput());
        dto.setOutput(example.getOutput());
        return dto;
    }

    private TestCaseDTO toDto(TestCase testCase) {
        TestCaseDTO dto = new TestCaseDTO();
        dto.setId(testCase.getId());
        dto.setInput(testCase.getInput());
        dto.setExpectedOutput(testCase.getExpectedOutput());
        return dto;
    }
}
