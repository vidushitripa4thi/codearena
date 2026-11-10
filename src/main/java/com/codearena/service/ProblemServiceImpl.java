package com.codearena.service;

import com.codearena.dto.PageResponse;
import com.codearena.dto.ProblemDTO;
import com.codearena.entity.Difficulty;
import com.codearena.entity.Problem;
import com.codearena.exception.ResourceNotFoundException;
import com.codearena.repository.ProblemRepository;
import com.codearena.repository.ProblemSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemMapper problemMapper;

    public ProblemServiceImpl(ProblemRepository problemRepository, ProblemMapper problemMapper) {
        this.problemRepository = problemRepository;
        this.problemMapper = problemMapper;
    }

    @Override
    @Transactional
    public ProblemDTO createProblem(ProblemDTO problemDTO) {
        Problem problem = problemMapper.toEntity(problemDTO);
        return problemMapper.toDto(problemRepository.save(problem));
    }

    @Override
    @Transactional
    public ProblemDTO updateProblem(Long id, ProblemDTO problemDTO) {
        Problem problem = findProblem(id);
        problemMapper.updateEntity(problem, problemDTO);
        return problemMapper.toDto(problemRepository.save(problem));
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemDTO getProblemById(Long id) {
        return problemMapper.toPublicDto(findProblem(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProblemDTO> getProblems(String search, Difficulty difficulty, Pageable pageable) {
        Specification<Problem> spec = ProblemSpecifications.withFilter(search, difficulty);
        Page<Problem> page = problemRepository.findAll(spec, pageable);
        Page<ProblemDTO> dtoPage = page.map(problemMapper::toPublicDto);
        return PageResponse.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemDTO> getAllProblems() {
        return problemRepository.findAll().stream().map(problemMapper::toPublicDto).toList();
    }

    @Override
    @Transactional
    public void deleteProblem(Long id) {
        if (!problemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Problem not found: " + id);
        }
        problemRepository.deleteById(id);
    }

    private Problem findProblem(Long id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found: " + id));
    }
}
