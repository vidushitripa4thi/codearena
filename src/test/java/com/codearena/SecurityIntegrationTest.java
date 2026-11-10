package com.codearena;

import com.codearena.dto.ProblemDTO;
import com.codearena.dto.SubmissionDTO;
import com.codearena.entity.Difficulty;
import com.codearena.entity.Language;
import com.codearena.entity.Problem;
import com.codearena.entity.Role;
import com.codearena.entity.Submission;
import com.codearena.entity.SubmissionStatus;
import com.codearena.entity.User;
import com.codearena.repository.ProblemRepository;
import com.codearena.repository.SubmissionRepository;
import com.codearena.repository.UserRepository;
import com.codearena.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        submissionRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void publicProblemsEndpointIsAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/api/problems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void publicProblemsEndpointFiltersByDifficultyAndSearch() throws Exception {
        Problem p1 = new Problem();
        p1.setTitle("Two Sum");
        p1.setDescription("Find two numbers.");
        p1.setDifficulty(Difficulty.EASY);
        p1.setTimeLimit(1);
        p1.setMemoryLimit(256);
        problemRepository.save(p1);

        Problem p2 = new Problem();
        p2.setTitle("Median of Two Sorted Arrays");
        p2.setDescription("Find median.");
        p2.setDifficulty(Difficulty.HARD);
        p2.setTimeLimit(2);
        p2.setMemoryLimit(512);
        problemRepository.save(p2);

        mockMvc.perform(get("/api/problems")
                        .param("search", "sum")
                        .param("difficulty", "EASY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Two Sum"))
                .andExpect(jsonPath("$.content[0].difficulty").value("EASY"));
    }

    @Test
    void meEndpointFailsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void meEndpointSucceedsWithValidToken() throws Exception {
        User user = new User();
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setPasswordHash(passwordEncoder.encode("secret123"));
        user.setRole(Role.USER);
        user.setCreatedAt(Instant.now());
        userRepository.save(user);

        String token = jwtService.generateToken("bob", "USER");

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void postProblemForbiddenForNormalUser() throws Exception {
        String token = jwtService.generateToken("bob", "USER");

        ProblemDTO problem = new ProblemDTO();
        problem.setTitle("Binary Search");
        problem.setDescription("Implement binary search.");
        problem.setDifficulty(Difficulty.EASY);
        problem.setTimeLimit(1);
        problem.setMemoryLimit(256);

        mockMvc.perform(post("/api/problems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(problem)))
                .andExpect(status().isForbidden());
    }

    @Test
    void postProblemAllowedForAdmin() throws Exception {
        String token = jwtService.generateToken("admin_user", "ADMIN");

        ProblemDTO problem = new ProblemDTO();
        problem.setTitle("Valid Palindrome");
        problem.setDescription("Check if string is palindrome.");
        problem.setDifficulty(Difficulty.EASY);
        problem.setTimeLimit(1);
        problem.setMemoryLimit(256);

        mockMvc.perform(post("/api/problems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(problem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Valid Palindrome"));
    }

    @Test
    void createAndGetSubmissionWorkflow() throws Exception {
        User user = new User();
        user.setUsername("coder_alice");
        user.setEmail("alice@example.com");
        user.setPasswordHash(passwordEncoder.encode("pass123"));
        user.setRole(Role.USER);
        user.setCreatedAt(Instant.now());
        userRepository.save(user);

        Problem problem = new Problem();
        problem.setTitle("Reverse String");
        problem.setDescription("Reverse string.");
        problem.setDifficulty(Difficulty.EASY);
        problem.setTimeLimit(1);
        problem.setMemoryLimit(256);
        problemRepository.save(problem);

        String token = jwtService.generateToken("coder_alice", "USER");

        SubmissionDTO.CreateRequest subRequest = new SubmissionDTO.CreateRequest();
        subRequest.setProblemId(problem.getId());
        subRequest.setLanguage(Language.JAVA);
        subRequest.setSourceCode("public class Solution {}");

        // 1. Submit code
        mockMvc.perform(post("/api/submissions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.username").value("coder_alice"));

        // 2. View my submissions
        mockMvc.perform(get("/api/submissions/my")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].problemTitle").value("Reverse String"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void getSubmissionByIdForbiddenForOtherUser() throws Exception {
        User owner = new User();
        owner.setUsername("owner_user");
        owner.setEmail("owner@example.com");
        owner.setPasswordHash(passwordEncoder.encode("pass123"));
        owner.setRole(Role.USER);
        owner.setCreatedAt(Instant.now());
        userRepository.save(owner);

        User attacker = new User();
        attacker.setUsername("attacker_user");
        attacker.setEmail("attacker@example.com");
        attacker.setPasswordHash(passwordEncoder.encode("pass123"));
        attacker.setRole(Role.USER);
        attacker.setCreatedAt(Instant.now());
        userRepository.save(attacker);

        Problem problem = new Problem();
        problem.setTitle("Problem 1");
        problem.setDescription("Description");
        problem.setDifficulty(Difficulty.EASY);
        problem.setTimeLimit(1);
        problem.setMemoryLimit(256);
        problemRepository.save(problem);

        Submission submission = new Submission();
        submission.setUser(owner);
        submission.setProblem(problem);
        submission.setSourceCode("my private secret code");
        submission.setLanguage(Language.PYTHON);
        submission.setStatus(SubmissionStatus.PENDING);
        submission.setSubmittedAt(Instant.now());
        submissionRepository.save(submission);

        String attackerToken = jwtService.generateToken("attacker_user", "USER");

        mockMvc.perform(get("/api/submissions/" + submission.getId())
                        .header("Authorization", "Bearer " + attackerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("You are not authorized to view this submission"));
    }
}
