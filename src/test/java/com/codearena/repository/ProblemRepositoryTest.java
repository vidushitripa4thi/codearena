package com.codearena.repository;

import com.codearena.entity.Difficulty;
import com.codearena.entity.Example;
import com.codearena.entity.Problem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProblemRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @Test
    void savesProblemWithExamples() {
        Problem problem = new Problem();
        problem.setTitle("Two Sum");
        problem.setDescription("Find two numbers that add up to target.");
        problem.setDifficulty(Difficulty.EASY);
        problem.setTimeLimit(1);
        problem.setMemoryLimit(256);

        Example example = new Example();
        example.setInput("4 9");
        example.setOutput("0 1");
        problem.addExample(example);

        Problem saved = problemRepository.saveAndFlush(problem);

        Optional<Problem> found = problemRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getExamples()).hasSize(1);
        assertThat(found.get().getExamples().get(0).getOutput()).isEqualTo("0 1");
    }
}
