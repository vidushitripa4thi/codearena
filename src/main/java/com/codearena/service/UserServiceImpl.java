package com.codearena.service;

import com.codearena.dto.AuthDTO;
import com.codearena.entity.Role;
import com.codearena.entity.User;
import com.codearena.exception.ResourceNotFoundException;
import com.codearena.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(java.time.Instant.now());

        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return new AuthDTO.AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResourceNotFoundException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return new AuthDTO.AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDTO.UserProfileDTO getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return new AuthDTO.UserProfileDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name(), user.getCreatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}