package com.melllon.newsdatacollectservice.service;

import com.melllon.newsdatacollectservice.dto.response.NewsArticleResponse;
import com.melllon.newsdatacollectservice.entity.NewsArticle;
import com.melllon.newsdatacollectservice.exception.UserNotFoundException;
import com.melllon.newsdatacollectservice.repository.NewsArticleRepository;
import com.melllon.newsdatacollectservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NewsArticleService {
    
    private final NewsArticleRepository newsArticleRepository;
    private final UserRepository userRepository;
    
    /**
     * 사용자의 모든 뉴스 아티클을 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<NewsArticleResponse> getUserNewsArticles(Long userId, int page, int size) {
        log.info("사용자 뉴스 아티클 조회: 사용자={}, 페이지={}, 크기={}", userId, page, size);
        
        // 사용자 존재 확인
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "collectedAt"));
        Page<NewsArticle> articles = newsArticleRepository.findByKeywordUserIdOrderByCollectedAtDesc(userId, pageable);
        
        return articles.map(this::convertToResponse);
    }
    
    /**
     * 특정 키워드의 뉴스 아티클을 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<NewsArticleResponse> getKeywordNewsArticles(Long keywordId, int page, int size) {
        log.info("키워드 뉴스 아티클 조회: 키워드={}, 페이지={}, 크기={}", keywordId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "collectedAt"));
        Page<NewsArticle> articles = newsArticleRepository.findByKeywordIdOrderByCollectedAtDesc(keywordId, pageable);
        
        return articles.map(this::convertToResponse);
    }
    
    /**
     * 최근 수집된 뉴스 아티클을 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<NewsArticleResponse> getRecentNewsArticles(Long userId, LocalDateTime since, int page, int size) {
        log.info("최근 뉴스 아티클 조회: 사용자={}, 이후={}, 페이지={}, 크기={}", userId, since, page, size);
        
        // 사용자 존재 확인
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "collectedAt"));
        Page<NewsArticle> articles = newsArticleRepository.findByUserIdAndCollectedAtSince(userId, since, pageable);
        
        return articles.map(this::convertToResponse);
    }
    
    /**
     * 특정 기간의 뉴스 아티클을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<NewsArticleResponse> getNewsArticlesByPeriod(Long keywordId, LocalDateTime start, LocalDateTime end) {
        log.info("기간별 뉴스 아티클 조회: 키워드={}, 시작={}, 종료={}", keywordId, start, end);
        
        List<NewsArticle> articles = newsArticleRepository.findByKeywordIdAndCollectedAtBetween(keywordId, start, end);
        
        return articles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 뉴스 아티클을 응답 DTO로 변환합니다.
     */
    private NewsArticleResponse convertToResponse(NewsArticle article) {
        return NewsArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .url(article.getUrl())
                .publishedAt(article.getPublishedAt())
                .collectedAt(article.getCollectedAt())
                .createdAt(article.getCreatedAt())
                .newsSiteName(article.getNewsSite().getName())
                .keyword(article.getKeyword().getKeyword())
                .build();
    }
} 