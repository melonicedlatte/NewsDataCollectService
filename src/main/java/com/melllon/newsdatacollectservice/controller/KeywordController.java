package com.melllon.newsdatacollectservice.controller;

import com.melllon.newsdatacollectservice.dto.request.KeywordRequest;
import com.melllon.newsdatacollectservice.dto.response.ApiResponse;
import com.melllon.newsdatacollectservice.dto.response.KeywordResponse;
import com.melllon.newsdatacollectservice.service.KeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
@Slf4j
public class KeywordController {
    
    private final KeywordService keywordService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<KeywordResponse>> createKeyword(
            @RequestParam Long userId,
            @Valid @RequestBody KeywordRequest request) {
        try {
            log.info("키워드 등록 요청: 사용자={}, 키워드={}", userId, request.getKeyword());
            
            KeywordResponse response = keywordService.createKeyword(userId, request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<KeywordResponse>builder()
                            .success(true)
                            .message("키워드가 등록되었습니다.")
                            .data(response)
                            .build());
        } catch (Exception e) {
            log.error("키워드 등록 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<KeywordResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getUserKeywords(@PathVariable Long userId) {
        try {
            log.info("사용자 키워드 목록 조회 요청: 사용자={}", userId);
            
            List<KeywordResponse> keywords = keywordService.getUserKeywords(userId);
            
            return ResponseEntity.ok(ApiResponse.<List<KeywordResponse>>builder()
                    .success(true)
                    .message("사용자 키워드 목록을 조회했습니다.")
                    .data(keywords)
                    .build());
        } catch (Exception e) {
            log.error("사용자 키워드 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<List<KeywordResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }
    
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getUserActiveKeywords(@PathVariable Long userId) {
        try {
            log.info("사용자 활성 키워드 목록 조회 요청: 사용자={}", userId);
            
            List<KeywordResponse> keywords = keywordService.getUserActiveKeywords(userId);
            
            return ResponseEntity.ok(ApiResponse.<List<KeywordResponse>>builder()
                    .success(true)
                    .message("사용자 활성 키워드 목록을 조회했습니다.")
                    .data(keywords)
                    .build());
        } catch (Exception e) {
            log.error("사용자 활성 키워드 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<List<KeywordResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }
    
    @GetMapping("/{keywordId}")
    public ResponseEntity<ApiResponse<KeywordResponse>> getKeywordById(@PathVariable Long keywordId) {
        try {
            KeywordResponse keyword = keywordService.getKeywordById(keywordId);
            
            return ResponseEntity.ok(ApiResponse.<KeywordResponse>builder()
                    .success(true)
                    .message("키워드를 조회했습니다.")
                    .data(keyword)
                    .build());
        } catch (Exception e) {
            log.error("키워드 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<KeywordResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }
    
    @PutMapping("/{keywordId}/status")
    public ResponseEntity<ApiResponse<KeywordResponse>> updateKeywordStatus(
            @PathVariable Long keywordId,
            @RequestParam boolean isActive) {
        try {
            log.info("키워드 상태 변경 요청: ID={}, isActive={}", keywordId, isActive);
            
            KeywordResponse response = keywordService.updateKeywordStatus(keywordId, isActive);
            
            return ResponseEntity.ok(ApiResponse.<KeywordResponse>builder()
                    .success(true)
                    .message("키워드 상태가 변경되었습니다.")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("키워드 상태 변경 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<KeywordResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }
    
    @DeleteMapping("/{keywordId}")
    public ResponseEntity<ApiResponse<Void>> deleteKeyword(@PathVariable Long keywordId) {
        try {
            log.info("키워드 삭제 요청: ID={}", keywordId);
            
            keywordService.deleteKeyword(keywordId);
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("키워드가 삭제되었습니다.")
                    .data(null)
                    .build());
        } catch (Exception e) {
            log.error("키워드 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }
} 