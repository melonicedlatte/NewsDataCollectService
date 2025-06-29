package com.melllon.newsdatacollectservice.service;

import com.melllon.newsdatacollectservice.dto.request.NewsSiteRequest;
import com.melllon.newsdatacollectservice.dto.response.NewsSiteResponse;
import com.melllon.newsdatacollectservice.entity.NewsSite;
import com.melllon.newsdatacollectservice.exception.NewsSiteAlreadyExistsException;
import com.melllon.newsdatacollectservice.exception.NewsSiteNotFoundException;
import com.melllon.newsdatacollectservice.repository.NewsSiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NewsSiteService {
    
    private final NewsSiteRepository newsSiteRepository;
    
    public NewsSiteResponse createNewsSite(NewsSiteRequest request) {
        log.info("뉴스 사이트 등록 요청: {}", request.getName());
        
        // 중복 검사
        if (newsSiteRepository.existsByName(request.getName())) {
            throw new NewsSiteAlreadyExistsException("이미 존재하는 뉴스 사이트입니다: " + request.getName());
        }
        
        // 뉴스 사이트 생성
        NewsSite newsSite = NewsSite.builder()
                .name(request.getName())
                .baseUrl(request.getBaseUrl())
                .searchUrlPattern(request.getSearchUrlPattern())
                .isActive(true)
                .build();
        
        NewsSite savedNewsSite = newsSiteRepository.save(newsSite);
        log.info("뉴스 사이트 등록 완료: {}", savedNewsSite.getName());
        
        return NewsSiteResponse.builder()
                .id(savedNewsSite.getId())
                .name(savedNewsSite.getName())
                .baseUrl(savedNewsSite.getBaseUrl())
                .searchUrlPattern(savedNewsSite.getSearchUrlPattern())
                .isActive(savedNewsSite.getIsActive())
                .createdAt(savedNewsSite.getCreatedAt())
                .build();
    }
    
    @Transactional(readOnly = true)
    public List<NewsSiteResponse> getAllActiveNewsSites() {
        log.info("활성 뉴스 사이트 목록 조회");
        
        List<NewsSite> newsSites = newsSiteRepository.findByIsActiveTrue();
        
        return newsSites.stream()
                .map(newsSite -> NewsSiteResponse.builder()
                        .id(newsSite.getId())
                        .name(newsSite.getName())
                        .baseUrl(newsSite.getBaseUrl())
                        .searchUrlPattern(newsSite.getSearchUrlPattern())
                        .isActive(newsSite.getIsActive())
                        .createdAt(newsSite.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public NewsSiteResponse getNewsSiteById(Long newsSiteId) {
        NewsSite newsSite = newsSiteRepository.findById(newsSiteId)
                .orElseThrow(() -> new NewsSiteNotFoundException("뉴스 사이트를 찾을 수 없습니다: " + newsSiteId));
        
        return NewsSiteResponse.builder()
                .id(newsSite.getId())
                .name(newsSite.getName())
                .baseUrl(newsSite.getBaseUrl())
                .searchUrlPattern(newsSite.getSearchUrlPattern())
                .isActive(newsSite.getIsActive())
                .createdAt(newsSite.getCreatedAt())
                .build();
    }
    
    public NewsSiteResponse updateNewsSiteStatus(Long newsSiteId, boolean isActive) {
        log.info("뉴스 사이트 상태 변경 요청: ID={}, isActive={}", newsSiteId, isActive);
        
        NewsSite newsSite = newsSiteRepository.findById(newsSiteId)
                .orElseThrow(() -> new NewsSiteNotFoundException("뉴스 사이트를 찾을 수 없습니다: " + newsSiteId));
        
        newsSite.setIsActive(isActive);
        NewsSite updatedNewsSite = newsSiteRepository.save(newsSite);
        
        log.info("뉴스 사이트 상태 변경 완료: {} -> {}", newsSite.getName(), isActive);
        
        return NewsSiteResponse.builder()
                .id(updatedNewsSite.getId())
                .name(updatedNewsSite.getName())
                .baseUrl(updatedNewsSite.getBaseUrl())
                .searchUrlPattern(updatedNewsSite.getSearchUrlPattern())
                .isActive(updatedNewsSite.getIsActive())
                .createdAt(updatedNewsSite.getCreatedAt())
                .build();
    }
} 