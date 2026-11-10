package com.codearena.service;

import com.codearena.dto.SubmissionDTO;
import com.codearena.entity.Role;
import com.codearena.entity.Submission;
import com.codearena.entity.SubmissionStatus;
import com.codearena.entity.Problem;
import com.codearena.entity.User;
import com.codearena.exception.ResourceNotFoundException;
import com.codearena.repository.ProblemRepository;
import com.codearena.repository.SubmissionRepository;
import com.codearena.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    public SubmissionServiceImpl(
            SubmissionRepository submissionRepository,
            UserRepository userRepository,
            ProblemRepository problemRepository
    ) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
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
        return mapToResponse(saved);
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
                submission.getSubmittedAt()
        );
    }
}
