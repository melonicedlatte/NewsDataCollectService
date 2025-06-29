package com.melllon.newsdatacollectservice.controller;

import com.melllon.newsdatacollectservice.dto.response.ApiResponse;
import com.melllon.newsdatacollectservice.dto.response.NewsArticleResponse;
import com.melllon.newsdatacollectservice.service.NewsArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/news-articles")
@RequiredArgsConstructor
@Slf4j
public class NewsArticleController {
    
    private final NewsArticleService newsArticleService;
    
    /**
     * 사용자의 모든 뉴스 아티클을 조회합니다.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<NewsArticleResponse>>> getUserNewsArticles(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.info("사용자 뉴스 아티클 조회 요청: 사용자={}, 페이지={}, 크기={}", userId, page, size);
            
            Page<NewsArticleResponse> articles = newsArticleService.getUserNewsArticles(userId, page, size);
            
            return ResponseEntity.ok(ApiResponse.<Page<NewsArticleResponse>>builder()
                    .success(true)
                    .message("사용자 뉴스 아티클을 조회했습니다.")
                    .data(articles)
                    .build());
        } catch (Exception e) {
            log.error("사용자 뉴스 아티클 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<Page<NewsArticleResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }
    
    /**
     * 특정 키워드의 뉴스 아티클을 조회합니다.
     */
    @GetMapping("/keyword/{keywordId}")
    public ResponseEntity<ApiResponse<Page<NewsArticleResponse>>> getKeywordNewsArticles(
            @PathVariable Long keywordId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.info("키워드 뉴스 아티클 조회 요청: 키워드={}, 페이지={}, 크기={}", keywordId, page, size);
            
            Page<NewsArticleResponse> articles = newsArticleService.getKeywordNewsArticles(keywordId, page, size);
            
            return ResponseEntity.ok(ApiResponse.<Page<NewsArticleResponse>>builder()
                    .success(true)
                    .message("키워드 뉴스 아티클을 조회했습니다.")
                    .data(articles)
                    .build());
        } catch (Exception e) {
            log.error("키워드 뉴스 아티클 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<Page<NewsArticleResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }
    
    /**
     * 최근 수집된 뉴스 아티클을 조회합니다.
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<ApiResponse<Page<NewsArticleResponse>>> getRecentNewsArticles(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.info("최근 뉴스 아티클 조회 요청: 사용자={}, 이후={}, 페이지={}, 크기={}", userId, since, page, size);
            
            Page<NewsArticleResponse> articles = newsArticleService.getRecentNewsArticles(userId, since, page, size);
            
            return ResponseEntity.ok(ApiResponse.<Page<NewsArticleResponse>>builder()
                    .success(true)
                    .message("최근 뉴스 아티클을 조회했습니다.")
                    .data(articles)
                    .build());
        } catch (Exception e) {
            log.error("최근 뉴스 아티클 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<Page<NewsArticleResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }
    
    /**
     * 특정 기간의 뉴스 아티클을 조회합니다.
     */
    @GetMapping("/keyword/{keywordId}/period")
    public ResponseEntity<ApiResponse<List<NewsArticleResponse>>> getNewsArticlesByPeriod(
            @PathVariable Long keywordId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            log.info("기간별 뉴스 아티클 조회 요청: 키워드={}, 시작={}, 종료={}", keywordId, start, end);
            
            List<NewsArticleResponse> articles = newsArticleService.getNewsArticlesByPeriod(keywordId, start, end);
            
            return ResponseEntity.ok(ApiResponse.<List<NewsArticleResponse>>builder()
                    .success(true)
                    .message("기간별 뉴스 아티클을 조회했습니다.")
                    .data(articles)
                    .build());
        } catch (Exception e) {
            log.error("기간별 뉴스 아티클 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<List<NewsArticleResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }
} 