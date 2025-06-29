package com.melllon.newsdatacollectservice.controller;

import com.melllon.newsdatacollectservice.dto.request.UserRegisterRequest;
import com.melllon.newsdatacollectservice.dto.request.UserLoginRequest;
import com.melllon.newsdatacollectservice.dto.response.ApiResponse;
import com.melllon.newsdatacollectservice.dto.response.UserResponse;
import com.melllon.newsdatacollectservice.dto.response.LoginResponse;
import com.melllon.newsdatacollectservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody UserRegisterRequest request) {
        log.info("회원가입 요청: {}", request.getUsername());
        
        try {
            UserResponse userResponse = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("회원가입이 완료되었습니다.", userResponse));
        } catch (Exception e) {
            log.error("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody UserLoginRequest request) {
        log.info("로그인 요청: {}", request.getUsername());
        
        try {
            LoginResponse loginResponse = userService.login(request);
            return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", loginResponse));
        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        try {
            UserResponse userResponse = userService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.success(userResponse));
        } catch (Exception e) {
            log.error("사용자 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
} 