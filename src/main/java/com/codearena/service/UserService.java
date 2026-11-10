package com.codearena.service;

import com.codearena.dto.AuthDTO;
import com.codearena.entity.User;

public interface UserService {
    AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request);
    AuthDTO.AuthResponse login(AuthDTO.LoginRequest request);
    AuthDTO.UserProfileDTO getProfile(String username);
    User findByUsername(String username);
}