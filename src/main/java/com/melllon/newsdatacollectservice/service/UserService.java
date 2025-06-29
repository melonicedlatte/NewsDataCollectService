package com.melllon.newsdatacollectservice.service;

import com.melllon.newsdatacollectservice.dto.request.UserRegisterRequest;
import com.melllon.newsdatacollectservice.dto.request.UserLoginRequest;
import com.melllon.newsdatacollectservice.dto.response.UserResponse;
import com.melllon.newsdatacollectservice.dto.response.LoginResponse;
import com.melllon.newsdatacollectservice.entity.User;
import com.melllon.newsdatacollectservice.exception.UserAlreadyExistsException;
import com.melllon.newsdatacollectservice.exception.UserNotFoundException;
import com.melllon.newsdatacollectservice.exception.InvalidCredentialsException;
import com.melllon.newsdatacollectservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserResponse register(UserRegisterRequest request) {
        log.info("회원가입 요청: {}", request.getUsername());
        
        // 중복 검사
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("이미 존재하는 사용자명입니다: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("이미 존재하는 이메일입니다: " + request.getEmail());
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        // 사용자 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("회원가입 완료: {}", savedUser.getUsername());
        
        return UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }
    
    public LoginResponse login(UserLoginRequest request) {
        log.info("로그인 요청: {}", request.getUsername());
        
        // 사용자 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("사용자명 또는 비밀번호가 올바르지 않습니다."));
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("사용자명 또는 비밀번호가 올바르지 않습니다.");
        }
        
        // 간단한 토큰 생성 (실제로는 JWT 사용 권장)
        String token = UUID.randomUUID().toString();
        
        log.info("로그인 성공: {}", user.getUsername());
        
        return LoginResponse.builder()
                .token(token)
                .user(UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .createdAt(user.getCreatedAt())
                        .build())
                .message("로그인이 완료되었습니다.")
                .build();
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + username));
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
} 