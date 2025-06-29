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
public class NewsSiteResponse {
    
    private Long id;
    private String name;
    private String baseUrl;
    private String searchUrlPattern;
    private Boolean isActive;
    private LocalDateTime createdAt;
} 