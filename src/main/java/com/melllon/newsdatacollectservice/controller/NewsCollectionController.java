package com.melllon.newsdatacollectservice.controller;

import com.melllon.newsdatacollectservice.dto.response.ApiResponse;
import com.melllon.newsdatacollectservice.service.NewsCollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news-collection")
@RequiredArgsConstructor
@Slf4j
public class NewsCollectionController {
    
    private final NewsCollectionService newsCollectionService;
    
    /**
     * 수동으로 뉴스 수집을 실행합니다.
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<String>> startNewsCollection() {
        try {
            log.info("수동 뉴스 수집 요청");
            
            // 비동기로 뉴스 수집 실행
            newsCollectionService.collectNewsForAllActiveKeywords();
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("뉴스 수집이 시작되었습니다.")
                    .data("뉴스 수집 작업이 백그라운드에서 실행 중입니다.")
                    .build());
        } catch (Exception e) {
            log.error("뉴스 수집 시작 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("뉴스 수집 시작 중 오류가 발생했습니다: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }
} 