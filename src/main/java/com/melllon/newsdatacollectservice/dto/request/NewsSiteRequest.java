package com.melllon.newsdatacollectservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsSiteRequest {
    
    @NotBlank(message = "사이트명은 필수입니다.")
    private String name;
    
    @NotBlank(message = "기본 URL은 필수입니다.")
    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$", 
             message = "올바른 URL 형식이 아닙니다.")
    private String baseUrl;
    
    @NotBlank(message = "검색 URL 패턴은 필수입니다.")
    private String searchUrlPattern;
} 