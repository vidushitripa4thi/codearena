package com.codearena;

import com.codearena.dto.AuthDTO;
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
    void putAndDeleteProblemForbiddenForNormalUser() throws Exception {
        Problem problem = new Problem();
        problem.setTitle("Initial Problem");
        problem.setDescription("Initial Description");
        problem.setDifficulty(Difficulty.EASY);
        problem.setTimeLimit(1);
        problem.setMemoryLimit(256);
        problem = problemRepository.save(problem);

        String userToken = jwtService.generateToken("regular_user", "USER");

        ProblemDTO updateDto = new ProblemDTO();
        updateDto.setTitle("Updated Title");
        updateDto.setDescription("Updated Description");
        updateDto.setDifficulty(Difficulty.MEDIUM);
        updateDto.setTimeLimit(2);
        updateDto.setMemoryLimit(512);

        // PUT forbidden
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/problems/" + problem.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());

        // DELETE forbidden
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/problems/" + problem.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void putAndDeleteProblemAllowedForAdmin() throws Exception {
        Problem problem = new Problem();
        problem.setTitle("Admin Problem");
        problem.setDescription("Admin Description");
        problem.setDifficulty(Difficulty.EASY);
        problem.setTimeLimit(1);
        problem.setMemoryLimit(256);
        problem = problemRepository.save(problem);

        String adminToken = jwtService.generateToken("super_admin", "ADMIN");

        ProblemDTO updateDto = new ProblemDTO();
        updateDto.setTitle("Updated Admin Problem");
        updateDto.setDescription("Updated Description");
        updateDto.setDifficulty(Difficulty.HARD);
        updateDto.setTimeLimit(2);
        updateDto.setMemoryLimit(512);

        // PUT allowed for admin
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/problems/" + problem.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Admin Problem"))
                .andExpect(jsonPath("$.difficulty").value("HARD"));

        // DELETE allowed for admin
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/problems/" + problem.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void getProblemDetailsReturnsExamplesAndConstraintsWithoutHiddenTestCases() throws Exception {
        Problem problem = new Problem();
        problem.setTitle("Public Problem Details Test");
        problem.setDescription("Problem with examples and test cases.");
        problem.setDifficulty(Difficulty.EASY);
        problem.setConstraints("1 <= n <= 100");
        problem.setInputFormat("Integer n");
        problem.setOutputFormat("Integer result");
        problem.setTimeLimit(1);
        problem.setMemoryLimit(256);

        com.codearena.entity.Example example = new com.codearena.entity.Example();
        example.setInput("n = 5");
        example.setOutput("10");
        problem.addExample(example);

        com.codearena.entity.TestCase testCase = new com.codearena.entity.TestCase();
        testCase.setInput("n = 100");
        testCase.setExpectedOutput("200");
        problem.addTestCase(testCase);

        problem = problemRepository.save(problem);

        // Publicly accessible without token
        mockMvc.perform(get("/api/problems/" + problem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Public Problem Details Test"))
                .andExpect(jsonPath("$.constraints").value("1 <= n <= 100"))
                .andExpect(jsonPath("$.examples").isArray())
                .andExpect(jsonPath("$.examples[0].input").value("n = 5"))
                .andExpect(jsonPath("$.examples[0].output").value("10"))
                .andExpect(jsonPath("$.testCases").isEmpty());
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

    @Test
    void swaggerDocumentationEndpointsAreAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("CodeArena API"))
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication']").exists())
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication'].scheme").value("bearer"));
    }

    @Test
    void protectedSubmissionEndpointsRemainProtectedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/submissions/my"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void successfulRegistrationReturnsTokenAndAllowsProfileAccess() throws Exception {
        AuthDTO.RegisterRequest regRequest = new AuthDTO.RegisterRequest();
        regRequest.setUsername("charlie_dev");
        regRequest.setEmail("charlie@example.com");
        regRequest.setPassword("strongPassword123");

        String responseJson = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.username").value("charlie_dev"))
                .andExpect(jsonPath("$.email").value("charlie@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andReturn().getResponse().getContentAsString();

        AuthDTO.AuthResponse authResponse = objectMapper.readValue(responseJson, AuthDTO.AuthResponse.class);

        // Verify token grants access to /api/auth/me
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + authResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("charlie_dev"))
                .andExpect(jsonPath("$.email").value("charlie@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void registrationFailsWithDuplicateUsername() throws Exception {
        AuthDTO.RegisterRequest firstUser = new AuthDTO.RegisterRequest();
        firstUser.setUsername("unique_user");
        firstUser.setEmail("first@example.com");
        firstUser.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstUser)))
                .andExpect(status().isOk());

        AuthDTO.RegisterRequest duplicateUser = new AuthDTO.RegisterRequest();
        duplicateUser.setUsername("unique_user");
        duplicateUser.setEmail("different@example.com");
        duplicateUser.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Username already exists"));
    }

    @Test
    void registrationFailsWithDuplicateEmail() throws Exception {
        AuthDTO.RegisterRequest firstUser = new AuthDTO.RegisterRequest();
        firstUser.setUsername("user_one");
        firstUser.setEmail("shared@example.com");
        firstUser.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstUser)))
                .andExpect(status().isOk());

        AuthDTO.RegisterRequest duplicateEmailUser = new AuthDTO.RegisterRequest();
        duplicateEmailUser.setUsername("user_two");
        duplicateEmailUser.setEmail("shared@example.com");
        duplicateEmailUser.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmailUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Email already exists"));
    }

    @Test
    void registrationFailsWithValidationErrors() throws Exception {
        AuthDTO.RegisterRequest invalidRequest = new AuthDTO.RegisterRequest();
        invalidRequest.setUsername("ab"); // min 3
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword("123"); // min 8

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").isString());
    }
}
