package com.codearena.controller;

import com.codearena.dto.PageResponse;
import com.codearena.dto.ProblemDTO;
import com.codearena.entity.Difficulty;
import com.codearena.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping
    public ResponseEntity<ProblemDTO> createProblem(@Valid @RequestBody ProblemDTO problemDTO) {
        ProblemDTO created = problemService.createProblem(problemDTO);
        return ResponseEntity.created(URI.create("/api/problems/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemDTO> getProblem(@PathVariable Long id) {
        return ResponseEntity.ok(problemService.getProblemById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProblemDTO>> listProblems(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String validatedSortBy = resolveSortProperty(sortBy);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(direction, validatedSortBy));
        return ResponseEntity.ok(problemService.getProblems(search, difficulty, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProblemDTO> updateProblem(
            @PathVariable Long id,
            @Valid @RequestBody ProblemDTO problemDTO
    ) {
        return ResponseEntity.ok(problemService.updateProblem(id, problemDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }

    private String resolveSortProperty(String sortBy) {
        if ("difficulty".equalsIgnoreCase(sortBy)) {
            return "difficulty";
        }
        if ("title".equalsIgnoreCase(sortBy)) {
            return "title";
        }
        if ("id".equalsIgnoreCase(sortBy)) {
            return "id";
        }
        return "createdAt";
    }
}
