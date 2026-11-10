package com.codearena.service;

import com.codearena.dto.PageResponse;
import com.codearena.dto.ProblemDTO;
import com.codearena.dto.TestCaseDTO;
import com.codearena.entity.Difficulty;
import com.codearena.entity.Problem;
import com.codearena.entity.TestCase;
import com.codearena.exception.ResourceNotFoundException;
import com.codearena.repository.ProblemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProblemServiceImplTest {

    @Mock
    private ProblemRepository problemRepository;

    private final ProblemMapper problemMapper = new ProblemMapper();

    private ProblemServiceImpl problemService;

    @Test
    void createProblemSavesMappedEntity() {
        problemService = new ProblemServiceImpl(problemRepository, problemMapper);
        when(problemRepository.save(any(Problem.class))).thenAnswer(invocation -> {
            Problem problem = invocation.getArgument(0);
            problem.setId(10L);
            return problem;
        });

        ProblemDTO request = sampleProblem();
        ProblemDTO created = problemService.createProblem(request);

        ArgumentCaptor<Problem> captor = ArgumentCaptor.forClass(Problem.class);
        verify(problemRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Two Sum");
        assertThat(created.getId()).isEqualTo(10L);
        assertThat(created.getDifficulty()).isEqualTo(Difficulty.EASY);
    }

    @Test
    void getProblemByIdThrowsWhenMissing() {
        problemService = new ProblemServiceImpl(problemRepository, problemMapper);
        when(problemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> problemService.getProblemById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Problem not found: 99");
    }

    @Test
    void getProblemByIdStripsHiddenTestCases() {
        problemService = new ProblemServiceImpl(problemRepository, problemMapper);

        Problem problem = new Problem();
        problem.setId(10L);
        problem.setTitle("Two Sum");
        problem.setDescription("Description");
        problem.setDifficulty(Difficulty.EASY);
        problem.setTimeLimit(1);
        problem.setMemoryLimit(256);
        problem.setCreatedAt(Instant.now());

        TestCase testCase = new TestCase();
        testCase.setId(1L);
        testCase.setInput("hidden-input");
        testCase.setExpectedOutput("hidden-output");
        problem.addTestCase(testCase);

        when(problemRepository.findById(10L)).thenReturn(Optional.of(problem));

        ProblemDTO result = problemService.getProblemById(10L);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getTestCases()).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    void getProblemsReturnsPaginatedResult() {
        problemService = new ProblemServiceImpl(problemRepository, problemMapper);

        Problem problem = new Problem();
        problem.setId(1L);
        problem.setTitle("Two Sum");
        problem.setDescription("Desc");
        problem.setDifficulty(Difficulty.EASY);
        problem.setTimeLimit(1);
        problem.setMemoryLimit(256);
        problem.setCreatedAt(Instant.now());

        Page<Problem> page = new PageImpl<>(List.of(problem), PageRequest.of(0, 10), 1);
        when(problemRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<ProblemDTO> response = problemService.getProblems("Two", Difficulty.EASY, PageRequest.of(0, 10));

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("Two Sum");
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getTestCases()).isEmpty();
    }

    private ProblemDTO sampleProblem() {
        ProblemDTO dto = new ProblemDTO();
        dto.setTitle("Two Sum");
        dto.setDescription("Find two numbers that add up to target.");
        dto.setDifficulty(Difficulty.EASY);
        dto.setTimeLimit(1);
        dto.setMemoryLimit(256);
        return dto;
    }
}
