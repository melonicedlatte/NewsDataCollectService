package com.melllon.newsdatacollectservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleResponse {
    
    private Long id;
    private String title;
    private String content;
    private String url;
    private LocalDateTime publishedAt;
    private String newsSiteName;
    private String keyword;
    private LocalDateTime collectedAt;
    private LocalDateTime createdAt;
} 