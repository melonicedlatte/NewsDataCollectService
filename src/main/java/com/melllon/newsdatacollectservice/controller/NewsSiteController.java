package com.melllon.newsdatacollectservice.controller;

import com.melllon.newsdatacollectservice.dto.request.NewsSiteRequest;
import com.melllon.newsdatacollectservice.dto.response.ApiResponse;
import com.melllon.newsdatacollectservice.dto.response.NewsSiteResponse;
import com.melllon.newsdatacollectservice.service.NewsSiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news-sites")
@RequiredArgsConstructor
@Slf4j
public class NewsSiteController {
    
    private final NewsSiteService newsSiteService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<NewsSiteResponse>> createNewsSite(@RequestBody NewsSiteRequest request) {
        log.info("뉴스 사이트 등록 요청: {}", request.getName());
        
        try {
            NewsSiteResponse newsSiteResponse = newsSiteService.createNewsSite(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("뉴스 사이트가 등록되었습니다.", newsSiteResponse));
        } catch (Exception e) {
            log.error("뉴스 사이트 등록 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<NewsSiteResponse>>> getAllActiveNewsSites() {
        log.info("활성 뉴스 사이트 목록 조회 요청");
        
        try {
            List<NewsSiteResponse> newsSites = newsSiteService.getAllActiveNewsSites();
            return ResponseEntity.ok(ApiResponse.success("활성 뉴스 사이트 목록을 조회했습니다.", newsSites));
        } catch (Exception e) {
            log.error("뉴스 사이트 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{newsSiteId}")
    public ResponseEntity<ApiResponse<NewsSiteResponse>> getNewsSiteById(@PathVariable Long newsSiteId) {
        log.info("뉴스 사이트 조회 요청: ID={}", newsSiteId);
        
        try {
            NewsSiteResponse newsSiteResponse = newsSiteService.getNewsSiteById(newsSiteId);
            return ResponseEntity.ok(ApiResponse.success(newsSiteResponse));
        } catch (Exception e) {
            log.error("뉴스 사이트 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{newsSiteId}/status")
    public ResponseEntity<ApiResponse<NewsSiteResponse>> updateNewsSiteStatus(
            @PathVariable Long newsSiteId,
            @RequestParam boolean isActive) {
        log.info("뉴스 사이트 상태 변경 요청: ID={}, isActive={}", newsSiteId, isActive);
        
        try {
            NewsSiteResponse newsSiteResponse = newsSiteService.updateNewsSiteStatus(newsSiteId, isActive);
            return ResponseEntity.ok(ApiResponse.success("뉴스 사이트 상태가 변경되었습니다.", newsSiteResponse));
        } catch (Exception e) {
            log.error("뉴스 사이트 상태 변경 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
} 