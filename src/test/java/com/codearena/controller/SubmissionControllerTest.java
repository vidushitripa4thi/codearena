package com.codearena.controller;

import com.codearena.dto.SubmissionDTO;
import com.codearena.entity.Language;
import com.codearena.entity.SubmissionStatus;
import com.codearena.service.SubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SubmissionController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(com.codearena.service.JwtService.class)
class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubmissionService submissionService;

    @Test
    @WithMockUser(username = "coder_john")
    void createSubmissionReturnsCreatedStatus() throws Exception {
        SubmissionDTO.CreateRequest request = new SubmissionDTO.CreateRequest();
        request.setProblemId(1L);
        request.setLanguage(Language.JAVA);
        request.setSourceCode("public class Solution {}");

        SubmissionDTO.Response response = new SubmissionDTO.Response(
                100L, 1L, "Two Sum", 1L, "coder_john",
                Language.JAVA, SubmissionStatus.PENDING, "public class Solution {}",
                null, null, Instant.now()
        );

        when(submissionService.createSubmission(any(SubmissionDTO.CreateRequest.class), eq("coder_john")))
                .thenReturn(response);

        mockMvc.perform(post("/api/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.language").value("JAVA"));
    }

    @Test
    @WithMockUser(username = "coder_john")
    void getMySubmissionsReturnsList() throws Exception {
        SubmissionDTO.Response response = new SubmissionDTO.Response(
                100L, 1L, "Two Sum", 1L, "coder_john",
                Language.JAVA, SubmissionStatus.PENDING, "public class Solution {}",
                null, null, Instant.now()
        );

        when(submissionService.getMySubmissions("coder_john")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/submissions/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].username").value("coder_john"));
    }

    @Test
    @WithMockUser(username = "coder_john")
    void getSubmissionByIdReturnsSubmission() throws Exception {
        SubmissionDTO.Response response = new SubmissionDTO.Response(
                100L, 1L, "Two Sum", 1L, "coder_john",
                Language.JAVA, SubmissionStatus.PENDING, "public class Solution {}",
                null, null, Instant.now()
        );

        when(submissionService.getSubmissionById(100L, "coder_john")).thenReturn(response);

        mockMvc.perform(get("/api/submissions/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.problemTitle").value("Two Sum"));
    }
}
