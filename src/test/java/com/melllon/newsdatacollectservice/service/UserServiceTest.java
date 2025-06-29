package com.melllon.newsdatacollectservice.service;

import com.melllon.newsdatacollectservice.dto.request.UserRegisterRequest;
import com.melllon.newsdatacollectservice.dto.request.UserLoginRequest;
import com.melllon.newsdatacollectservice.dto.response.LoginResponse;
import com.melllon.newsdatacollectservice.dto.response.UserResponse;
import com.melllon.newsdatacollectservice.entity.User;
import com.melllon.newsdatacollectservice.exception.UserAlreadyExistsException;
import com.melllon.newsdatacollectservice.exception.InvalidCredentialsException;
import com.melllon.newsdatacollectservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User testUser;
    private UserRegisterRequest registerRequest;
    private UserLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();

        registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new UserLoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_Success() {
        // given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        UserResponse result = userService.register(registerRequest);

        // then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UserAlreadyExists() {
        // given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // when & then
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.register(registerRequest);
        });
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_UserNotFound() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // when & then
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.login(loginRequest);
        });
        verify(userRepository).findByUsername("testuser");
    }
} 