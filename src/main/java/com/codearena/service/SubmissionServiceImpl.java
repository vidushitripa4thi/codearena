package com.codearena.service;

import com.codearena.dto.SubmissionDTO;
import com.codearena.entity.Problem;
import com.codearena.entity.Role;
import com.codearena.entity.Submission;
import com.codearena.entity.SubmissionStatus;
import com.codearena.entity.User;
import com.codearena.exception.ResourceNotFoundException;
import com.codearena.repository.ProblemRepository;
import com.codearena.repository.SubmissionRepository;
import com.codearena.repository.UserRepository;
import com.codearena.service.execution.CodeExecutionService;
import com.codearena.service.execution.ExecutionResult;
import com.codearena.service.execution.TestCaseItem;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final CodeExecutionService codeExecutionService;

    public SubmissionServiceImpl(
            SubmissionRepository submissionRepository,
            UserRepository userRepository,
            ProblemRepository problemRepository,
            CodeExecutionService codeExecutionService
    ) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.codeExecutionService = codeExecutionService;
    }

    @Override
    @Transactional
    public SubmissionDTO.Response createSubmission(SubmissionDTO.CreateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + request.getProblemId()));

        Submission submission = new Submission();
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setSourceCode(request.getSourceCode());
        submission.setLanguage(request.getLanguage());
        submission.setStatus(SubmissionStatus.PENDING);
        submission.setSubmittedAt(Instant.now());

        Submission saved = submissionRepository.save(submission);

        // Execute against all configured test cases (or sample examples if test_cases empty)
        List<TestCaseItem> testCaseItems = new ArrayList<>();
        if (problem.getTestCases() != null && !problem.getTestCases().isEmpty()) {
            problem.getTestCases().forEach(tc ->
                    testCaseItems.add(new TestCaseItem(tc.getId(), tc.getInput(), tc.getExpectedOutput()))
            );
        } else if (problem.getExamples() != null && !problem.getExamples().isEmpty()) {
            problem.getExamples().forEach(ex ->
                    testCaseItems.add(new TestCaseItem(ex.getId(), ex.getInput(), ex.getOutput()))
            );
        }

        if (testCaseItems.isEmpty()) {
            return mapToResponse(saved);
        }

        int timeLimit = problem.getTimeLimit() != null ? problem.getTimeLimit() : 2;
        int memoryLimit = problem.getMemoryLimit() != null ? problem.getMemoryLimit() : 256;

        ExecutionResult execResult = codeExecutionService.execute(
                request.getSourceCode(),
                request.getLanguage(),
                testCaseItems,
                timeLimit,
                memoryLimit
        );

        saved.setStatus(execResult.getStatus());
        saved.setExecutionTime((int) (long) execResult.getExecutionTimeMs());
        saved.setMemoryUsed((int) (execResult.getMemoryUsedKb() / 1024));

        Submission updated = submissionRepository.save(saved);
        return mapToResponse(updated, execResult);
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionDTO.ExecutionResponse runCode(SubmissionDTO.CreateRequest request, String username) {
        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + request.getProblemId()));

        // Run code evaluates against sample test cases (examples)
        List<TestCaseItem> exampleItems = new ArrayList<>();
        if (problem.getExamples() != null && !problem.getExamples().isEmpty()) {
            problem.getExamples().forEach(ex ->
                    exampleItems.add(new TestCaseItem(ex.getId(), ex.getInput(), ex.getOutput()))
            );
        } else if (problem.getTestCases() != null && !problem.getTestCases().isEmpty()) {
            problem.getTestCases().stream().limit(2).forEach(tc ->
                    exampleItems.add(new TestCaseItem(tc.getId(), tc.getInput(), tc.getExpectedOutput()))
            );
        }

        int timeLimit = problem.getTimeLimit() != null ? problem.getTimeLimit() : 2;
        int memoryLimit = problem.getMemoryLimit() != null ? problem.getMemoryLimit() : 256;

        ExecutionResult execResult = codeExecutionService.execute(
                request.getSourceCode(),
                request.getLanguage(),
                exampleItems,
                timeLimit,
                memoryLimit
        );

        return new SubmissionDTO.ExecutionResponse(
                execResult.getStatus(),
                execResult.getCompileError(),
                execResult.getExecutionTimeMs(),
                execResult.getTotalTestCases(),
                execResult.getPassedTestCases(),
                execResult.getTestCaseResults()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionDTO.Response> getMySubmissions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return submissionRepository.findByUserIdOrderBySubmittedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionDTO.Response getSubmissionById(Long id, String username) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + id));

        User requestingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        boolean isOwner = submission.getUser().getId().equals(requestingUser.getId());
        boolean isAdmin = requestingUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to view this submission");
        }

        return mapToResponse(submission);
    }

    private SubmissionDTO.Response mapToResponse(Submission submission) {
        return mapToResponse(submission, null);
    }

    private SubmissionDTO.Response mapToResponse(Submission submission, ExecutionResult execResult) {
        return new SubmissionDTO.Response(
                submission.getId(),
                submission.getProblem().getId(),
                submission.getProblem().getTitle(),
                submission.getUser().getId(),
                submission.getUser().getUsername(),
                submission.getLanguage(),
                submission.getStatus(),
                submission.getSourceCode(),
                submission.getExecutionTime(),
                submission.getMemoryUsed(),
                submission.getSubmittedAt(),
                execResult != null ? execResult.getCompileError() : null,
                execResult != null ? execResult.getTotalTestCases() : null,
                execResult != null ? execResult.getPassedTestCases() : null,
                execResult != null ? execResult.getTestCaseResults() : null
        );
    }
}
