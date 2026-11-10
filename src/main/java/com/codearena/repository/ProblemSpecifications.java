package com.codearena.repository;

import com.codearena.entity.Difficulty;
import com.codearena.entity.Problem;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProblemSpecifications {

    public static Specification<Problem> withFilter(String search, Difficulty difficulty) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("title")), pattern));
            }

            if (difficulty != null) {
                predicates.add(cb.equal(root.get("difficulty"), difficulty));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
