package com.codearena.controller;

import com.codearena.dto.AuthDTO;
import com.codearena.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(com.codearena.service.JwtService.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void registerReturnsAuthResponse() throws Exception {
        AuthDTO.RegisterRequest request = new AuthDTO.RegisterRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("password123");

        AuthDTO.AuthResponse response = new AuthDTO.AuthResponse("mock-jwt-token", "alice", "alice@example.com", "USER");
        when(userService.register(any(AuthDTO.RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void loginReturnsAuthResponse() throws Exception {
        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setUsername("alice");
        request.setPassword("password123");

        AuthDTO.AuthResponse response = new AuthDTO.AuthResponse("mock-jwt-token", "alice", "alice@example.com", "USER");
        when(userService.login(any(AuthDTO.LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    @WithMockUser(username = "alice", roles = {"USER"})
    void getProfileReturnsUserProfileWhenAuthenticated() throws Exception {
        AuthDTO.UserProfileDTO profile = new AuthDTO.UserProfileDTO(1L, "alice", "alice@example.com", "USER", Instant.now());
        when(userService.getProfile("alice")).thenReturn(profile);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
