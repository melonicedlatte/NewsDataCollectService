package com.melllon.newsdatacollectservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordRequest {
    
    @NotBlank(message = "키워드는 필수입니다.")
    @Size(min = 1, max = 100, message = "키워드는 1자 이상 100자 이하여야 합니다.")
    private String keyword;
} 