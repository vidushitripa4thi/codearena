package com.codearena.service;

import com.codearena.dto.SubmissionDTO;
import com.codearena.entity.Difficulty;
import com.codearena.entity.Language;
import com.codearena.entity.Problem;
import com.codearena.entity.Role;
import com.codearena.entity.Submission;
import com.codearena.entity.SubmissionStatus;
import com.codearena.entity.User;
import com.codearena.exception.ResourceNotFoundException;
import com.codearena.repository.ProblemRepository;
import com.codearena.repository.SubmissionRepository;
import com.codearena.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceImplTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private com.codearena.service.execution.CodeExecutionService codeExecutionService;

    private SubmissionServiceImpl submissionService;

    @BeforeEach
    void setUp() {
        submissionService = new SubmissionServiceImpl(submissionRepository, userRepository, problemRepository, codeExecutionService);
    }

    @Test
    void createSubmissionSavesWithPendingStatus() {
        User user = new User();
        user.setId(1L);
        user.setUsername("coder_john");

        Problem problem = new Problem();
        problem.setId(10L);
        problem.setTitle("Two Sum");
        com.codearena.entity.TestCase tc = new com.codearena.entity.TestCase();
        tc.setId(1L);
        tc.setInput("nums = [2,7,11,15], target = 9");
        tc.setExpectedOutput("[0,1]");
        problem.addTestCase(tc);

        when(userRepository.findByUsername("coder_john")).thenReturn(Optional.of(user));
        when(problemRepository.findById(10L)).thenReturn(Optional.of(problem));
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> {
            Submission s = invocation.getArgument(0);
            s.setId(100L);
            return s;
        });

        com.codearena.service.execution.ExecutionResult execRes = new com.codearena.service.execution.ExecutionResult();
        execRes.setStatus(SubmissionStatus.ACCEPTED);
        execRes.setExecutionTimeMs(45L);
        when(codeExecutionService.execute(any(), any(), any(), anyInt(), anyInt())).thenReturn(execRes);

        SubmissionDTO.CreateRequest request = new SubmissionDTO.CreateRequest();
        request.setProblemId(10L);
        request.setLanguage(Language.JAVA);
        request.setSourceCode("public class Solution {}");

        SubmissionDTO.Response response = submissionService.createSubmission(request, "coder_john");

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getProblemId()).isEqualTo(10L);
        assertThat(response.getProblemTitle()).isEqualTo("Two Sum");
        assertThat(response.getUsername()).isEqualTo("coder_john");
        assertThat(response.getStatus()).isEqualTo(SubmissionStatus.ACCEPTED);
        assertThat(response.getLanguage()).isEqualTo(Language.JAVA);
        assertThat(response.getSourceCode()).isEqualTo("public class Solution {}");
    }

    @Test
    void runCodeEvaluatesAgainstExamples() {
        Problem problem = new Problem();
        problem.setId(10L);
        problem.setTitle("Two Sum");
        com.codearena.entity.TestCase tc = new com.codearena.entity.TestCase();
        tc.setId(1L);
        tc.setInput("nums = [2,7,11,15], target = 9");
        tc.setExpectedOutput("[0,1]");
        problem.addTestCase(tc);

        com.codearena.entity.Example ex = new com.codearena.entity.Example();
        ex.setId(1L);
        ex.setInput("nums = [2,7,11,15], target = 9");
        ex.setOutput("[0,1]");
        problem.addExample(ex);

        when(problemRepository.findById(10L)).thenReturn(Optional.of(problem));

        com.codearena.service.execution.ExecutionResult execRes = new com.codearena.service.execution.ExecutionResult();
        execRes.setStatus(SubmissionStatus.ACCEPTED);
        execRes.setExecutionTimeMs(30L);
        execRes.setTotalTestCases(1);
        execRes.setPassedTestCases(1);
        when(codeExecutionService.execute(any(), any(), any(), anyInt(), anyInt())).thenReturn(execRes);

        SubmissionDTO.CreateRequest request = new SubmissionDTO.CreateRequest();
        request.setProblemId(10L);
        request.setLanguage(Language.JAVA);
        request.setSourceCode("public class Solution {}");

        SubmissionDTO.ExecutionResponse response = submissionService.runCode(request, "coder_john");

        assertThat(response.getStatus()).isEqualTo(SubmissionStatus.ACCEPTED);
        assertThat(response.getExecutionTimeMs()).isEqualTo(30L);
        assertThat(response.getTotalTestCases()).isEqualTo(1);
        assertThat(response.getPassedTestCases()).isEqualTo(1);
    }

    @Test
    void createSubmissionThrowsWhenProblemMissing() {
        User user = new User();
        user.setUsername("coder_john");
        when(userRepository.findByUsername("coder_john")).thenReturn(Optional.of(user));
        when(problemRepository.findById(99L)).thenReturn(Optional.empty());

        SubmissionDTO.CreateRequest request = new SubmissionDTO.CreateRequest();
        request.setProblemId(99L);
        request.setLanguage(Language.PYTHON);
        request.setSourceCode("print('hello')");

        assertThatThrownBy(() -> submissionService.createSubmission(request, "coder_john"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Problem not found with id: 99");
    }

    @Test
    void getMySubmissionsReturnsUserSubmissions() {
        User user = new User();
        user.setId(1L);
        user.setUsername("coder_john");

        Problem problem = new Problem();
        problem.setId(10L);
        problem.setTitle("Two Sum");
        com.codearena.entity.TestCase tc = new com.codearena.entity.TestCase();
        tc.setId(1L);
        tc.setInput("nums = [2,7,11,15], target = 9");
        tc.setExpectedOutput("[0,1]");
        problem.addTestCase(tc);

        Submission submission = new Submission();
        submission.setId(100L);
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setLanguage(Language.JAVA);
        submission.setStatus(SubmissionStatus.PENDING);
        submission.setSourceCode("code");
        submission.setSubmittedAt(Instant.now());

        when(userRepository.findByUsername("coder_john")).thenReturn(Optional.of(user));
        when(submissionRepository.findByUserIdOrderBySubmittedAtDesc(1L)).thenReturn(List.of(submission));

        List<SubmissionDTO.Response> results = submissionService.getMySubmissions("coder_john");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(100L);
        assertThat(results.get(0).getUsername()).isEqualTo("coder_john");
    }

    @Test
    void getSubmissionByIdAllowsOwner() {
        User user = new User();
        user.setId(1L);
        user.setUsername("coder_john");
        user.setRole(Role.USER);

        Problem problem = new Problem();
        problem.setId(10L);
        problem.setTitle("Two Sum");
        com.codearena.entity.TestCase tc = new com.codearena.entity.TestCase();
        tc.setId(1L);
        tc.setInput("nums = [2,7,11,15], target = 9");
        tc.setExpectedOutput("[0,1]");
        problem.addTestCase(tc);

        Submission submission = new Submission();
        submission.setId(100L);
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setLanguage(Language.JAVA);
        submission.setStatus(SubmissionStatus.PENDING);
        submission.setSourceCode("code");

        when(submissionRepository.findById(100L)).thenReturn(Optional.of(submission));
        when(userRepository.findByUsername("coder_john")).thenReturn(Optional.of(user));

        SubmissionDTO.Response response = submissionService.getSubmissionById(100L, "coder_john");

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getUsername()).isEqualTo("coder_john");
    }

    @Test
    void getSubmissionByIdAllowsAdmin() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("coder_john");
        owner.setRole(Role.USER);

        User admin = new User();
        admin.setId(2L);
        admin.setUsername("admin_user");
        admin.setRole(Role.ADMIN);

        Problem problem = new Problem();
        problem.setId(10L);
        problem.setTitle("Two Sum");
        com.codearena.entity.TestCase tc = new com.codearena.entity.TestCase();
        tc.setId(1L);
        tc.setInput("nums = [2,7,11,15], target = 9");
        tc.setExpectedOutput("[0,1]");
        problem.addTestCase(tc);

        Submission submission = new Submission();
        submission.setId(100L);
        submission.setUser(owner);
        submission.setProblem(problem);
        submission.setLanguage(Language.CPP);
        submission.setStatus(SubmissionStatus.PENDING);
        submission.setSourceCode("int main() {}");

        when(submissionRepository.findById(100L)).thenReturn(Optional.of(submission));
        when(userRepository.findByUsername("admin_user")).thenReturn(Optional.of(admin));

        SubmissionDTO.Response response = submissionService.getSubmissionById(100L, "admin_user");

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getUsername()).isEqualTo("coder_john");
    }

    @Test
    void getSubmissionByIdThrowsAccessDeniedForNonOwnerNonAdmin() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("coder_john");
        owner.setRole(Role.USER);

        User otherUser = new User();
        otherUser.setId(3L);
        otherUser.setUsername("hacker_dan");
        otherUser.setRole(Role.USER);

        Problem problem = new Problem();
        problem.setId(10L);
        problem.setTitle("Two Sum");
        com.codearena.entity.TestCase tc = new com.codearena.entity.TestCase();
        tc.setId(1L);
        tc.setInput("nums = [2,7,11,15], target = 9");
        tc.setExpectedOutput("[0,1]");
        problem.addTestCase(tc);

        Submission submission = new Submission();
        submission.setId(100L);
        submission.setUser(owner);
        submission.setProblem(problem);
        submission.setLanguage(Language.CPP);
        submission.setStatus(SubmissionStatus.PENDING);

        when(submissionRepository.findById(100L)).thenReturn(Optional.of(submission));
        when(userRepository.findByUsername("hacker_dan")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> submissionService.getSubmissionById(100L, "hacker_dan"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You are not authorized to view this submission");
    }
}
