package com.codearena.service;

import com.codearena.dto.AuthDTO;
import com.codearena.entity.Role;
import com.codearena.entity.User;
import com.codearena.exception.ResourceNotFoundException;
import com.codearena.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private UserServiceImpl userService;

    @Test
    void registerCreatesUserAndReturnsToken() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, jwtService);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtService.generateToken("testuser", "USER")).thenReturn("jwt-token");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setRole(Role.USER);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthDTO.RegisterRequest request = new AuthDTO.RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthDTO.AuthResponse response = userService.register(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRole()).isEqualTo("USER");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("testuser");
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("encodedPassword");
        assertThat(captor.getValue().getRole()).isEqualTo(Role.USER);
    }

    @Test
    void registerThrowsWhenUsernameExists() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, jwtService);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        AuthDTO.RegisterRequest request = new AuthDTO.RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists");
    }

    @Test
    void registerThrowsWhenEmailExists() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, jwtService);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        AuthDTO.RegisterRequest request = new AuthDTO.RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");
    }

    @Test
    void loginReturnsTokenWhenCredentialsValid() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, jwtService);
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("encodedPassword");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("testuser", "USER")).thenReturn("jwt-token");

        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        AuthDTO.AuthResponse response = userService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRole()).isEqualTo("USER");
    }

    @Test
    void loginThrowsWhenUserNotFound() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, jwtService);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void loginThrowsWhenPasswordInvalid() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, jwtService);
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void getProfileReturnsUserProfile() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, jwtService);
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        user.setCreatedAt(java.time.Instant.now());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        AuthDTO.UserProfileDTO profile = userService.getProfile("testuser");

        assertThat(profile.getId()).isEqualTo(1L);
        assertThat(profile.getUsername()).isEqualTo("testuser");
        assertThat(profile.getEmail()).isEqualTo("test@example.com");
        assertThat(profile.getRole()).isEqualTo("USER");
    }
}