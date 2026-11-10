package com.codearena.service;

import com.codearena.dto.PageResponse;
import com.codearena.dto.ProblemDTO;
import com.codearena.entity.Difficulty;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProblemService {

    ProblemDTO createProblem(ProblemDTO problemDTO);

    ProblemDTO updateProblem(Long id, ProblemDTO problemDTO);

    ProblemDTO getProblemById(Long id);

    PageResponse<ProblemDTO> getProblems(String search, Difficulty difficulty, Pageable pageable);

    List<ProblemDTO> getAllProblems();

    void deleteProblem(Long id);
}
