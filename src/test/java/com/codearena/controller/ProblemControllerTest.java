package com.codearena.controller;

import com.codearena.dto.ProblemDTO;
import com.codearena.entity.Difficulty;
import com.codearena.exception.ResourceNotFoundException;
import com.codearena.service.ProblemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = ProblemController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    },
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.codearena.config.SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.codearena\\.config\\..*")
    }
)
class ProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProblemService problemService;

    @Test
    void createProblemReturnsCreatedProblem() throws Exception {
        ProblemDTO created = sampleProblem();
        created.setId(42L);
        when(problemService.createProblem(any(ProblemDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProblem())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/problems/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.title").value("Two Sum"));
    }

    @Test
    void createProblemReturnsBadRequestForMissingTitle() throws Exception {
        ProblemDTO request = sampleProblem();
        request.setTitle("");

        mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("title=Title is required")));
    }

    @Test
    void listProblemsReturnsProblems() throws Exception {
        ProblemDTO problem = sampleProblem();
        problem.setId(1L);
        com.codearena.dto.PageResponse<ProblemDTO> pageResponse = new com.codearena.dto.PageResponse<>(
                List.of(problem), 0, 10, 1, 1, false, false
        );
        when(problemService.getProblems(any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/problems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].difficulty").value("EASY"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getProblemReturnsNotFoundProblemDetail() throws Exception {
        when(problemService.getProblemById(123L)).thenThrow(new ResourceNotFoundException("Problem not found: 123"));

        mockMvc.perform(get("/api/problems/123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Problem not found: 123"))
                .andExpect(jsonPath("$.instance").value("/api/problems/123"));
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