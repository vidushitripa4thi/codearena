package com.codearena.service;

import com.codearena.dto.SubmissionDTO;

import java.util.List;

public interface SubmissionService {
    SubmissionDTO.Response createSubmission(SubmissionDTO.CreateRequest request, String username);
    SubmissionDTO.ExecutionResponse runCode(SubmissionDTO.CreateRequest request, String username);
    List<SubmissionDTO.Response> getMySubmissions(String username);
    SubmissionDTO.Response getSubmissionById(Long id, String username);
}
