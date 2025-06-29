package com.melllon.newsdatacollectservice.repository;

import com.melllon.newsdatacollectservice.entity.NewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    
    Page<NewsArticle> findByKeywordIdOrderByCollectedAtDesc(Long keywordId, Pageable pageable);
    
    Page<NewsArticle> findByKeywordUserIdOrderByCollectedAtDesc(Long userId, Pageable pageable);
    
    @Query("SELECT na FROM NewsArticle na WHERE na.keyword.user.id = :userId " +
           "AND na.collectedAt >= :since ORDER BY na.collectedAt DESC")
    Page<NewsArticle> findByUserIdAndCollectedAtSince(
            @Param("userId") Long userId, 
            @Param("since") LocalDateTime since, 
            Pageable pageable);
    
    boolean existsByUrl(String url);
    
    List<NewsArticle> findByKeywordIdAndCollectedAtBetween(
            Long keywordId, LocalDateTime start, LocalDateTime end);
} 