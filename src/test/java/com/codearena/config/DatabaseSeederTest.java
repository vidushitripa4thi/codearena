package com.codearena.config;

import com.codearena.entity.Problem;
import com.codearena.entity.Role;
import com.codearena.entity.User;
import com.codearena.repository.ProblemRepository;
import com.codearena.repository.SubmissionRepository;
import com.codearena.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DatabaseSeederTest {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private DatabaseSeeder seeder;

    @BeforeEach
    void setUp() {
        submissionRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();

        seeder = new DatabaseSeeder(problemRepository, userRepository, passwordEncoder);
        ReflectionTestUtils.setField(seeder, "seedEnabled", true);
    }

    @Test
    void seedProblemsInsertsTenProblemsWithExamplesAndTestCases() {
        seeder.seedProblems();

        List<Problem> problems = problemRepository.findAll();
        assertThat(problems).hasSize(10);

        for (Problem problem : problems) {
            assertThat(problem.getTitle()).isNotBlank();
            assertThat(problem.getDescription()).isNotBlank();
            assertThat(problem.getDifficulty()).isNotNull();
            assertThat(problem.getConstraints()).isNotBlank();
            assertThat(problem.getInputFormat()).isNotBlank();
            assertThat(problem.getOutputFormat()).isNotBlank();
            assertThat(problem.getTimeLimit()).isPositive();
            assertThat(problem.getMemoryLimit()).isPositive();
            assertThat(problem.getExamples()).hasSizeGreaterThanOrEqualTo(2);
            assertThat(problem.getTestCases()).isNotEmpty();
        }
    }

    @Test
    void seedProblemsIsIdempotent() {
        // First run
        seeder.seedProblems();
        assertThat(problemRepository.count()).isEqualTo(10);

        // Second run
        seeder.seedProblems();
        assertThat(problemRepository.count()).isEqualTo(10);
    }

    @Test
    void seedAdminCreatesAdminAccountWhenCredentialsSupplied() {
        ReflectionTestUtils.setField(seeder, "adminUsername", "admin_dev");
        ReflectionTestUtils.setField(seeder, "adminEmail", "admin@codearena.com");
        ReflectionTestUtils.setField(seeder, "adminPassword", "SecureAdminPass123!");

        seeder.seedAdmin();

        User admin = userRepository.findByUsername("admin_dev").orElseThrow();
        assertThat(admin.getEmail()).isEqualTo("admin@codearena.com");
        assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
        assertThat(passwordEncoder.matches("SecureAdminPass123!", admin.getPasswordHash())).isTrue();
    }

    @Test
    void seedAdminIsIdempotent() {
        ReflectionTestUtils.setField(seeder, "adminUsername", "admin_dev");
        ReflectionTestUtils.setField(seeder, "adminEmail", "admin@codearena.com");
        ReflectionTestUtils.setField(seeder, "adminPassword", "SecureAdminPass123!");

        seeder.seedAdmin();
        assertThat(userRepository.count()).isEqualTo(1);

        // Run again
        seeder.seedAdmin();
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void seedAdminSkipsWhenCredentialsNotProvided() {
        ReflectionTestUtils.setField(seeder, "adminUsername", "");
        ReflectionTestUtils.setField(seeder, "adminPassword", "");

        seeder.seedAdmin();
        assertThat(userRepository.count()).isZero();
    }

    @Test
    void runSkipsWhenSeedDisabled() {
        ReflectionTestUtils.setField(seeder, "seedEnabled", false);
        seeder.run();

        assertThat(problemRepository.count()).isZero();
        assertThat(userRepository.count()).isZero();
    }
}
