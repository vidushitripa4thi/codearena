package com.codearena.repository;

import com.codearena.entity.Submission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @EntityGraph(attributePaths = {"user", "problem"})
    List<Submission> findByUserIdOrderBySubmittedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"user", "problem"})
    List<Submission> findByProblemIdOrderBySubmittedAtDesc(Long problemId);

    @Override
    @EntityGraph(attributePaths = {"user", "problem"})
    Optional<Submission> findById(Long id);
}
