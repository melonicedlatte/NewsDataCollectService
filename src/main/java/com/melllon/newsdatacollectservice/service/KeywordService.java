package com.melllon.newsdatacollectservice.service;

import com.melllon.newsdatacollectservice.dto.request.KeywordRequest;
import com.melllon.newsdatacollectservice.dto.response.KeywordResponse;
import com.melllon.newsdatacollectservice.entity.Keyword;
import com.melllon.newsdatacollectservice.entity.User;
import com.melllon.newsdatacollectservice.exception.KeywordAlreadyExistsException;
import com.melllon.newsdatacollectservice.exception.KeywordNotFoundException;
import com.melllon.newsdatacollectservice.exception.UserNotFoundException;
import com.melllon.newsdatacollectservice.repository.KeywordRepository;
import com.melllon.newsdatacollectservice.repository.UserRepository;
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
public class KeywordService {
    
    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository;
    
    public KeywordResponse createKeyword(Long userId, KeywordRequest request) {
        log.info("키워드 등록 요청: 사용자={}, 키워드={}", userId, request.getKeyword());
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        // 중복 검사 (같은 사용자의 같은 키워드)
        if (keywordRepository.existsByUserAndKeyword(user, request.getKeyword())) {
            throw new KeywordAlreadyExistsException("이미 존재하는 키워드입니다: " + request.getKeyword());
        }
        
        // 키워드 생성
        Keyword keyword = Keyword.builder()
                .user(user)
                .keyword(request.getKeyword())
                .isActive(true)
                .build();
        
        Keyword savedKeyword = keywordRepository.save(keyword);
        log.info("키워드 등록 완료: 사용자={}, 키워드={}", userId, savedKeyword.getKeyword());
        
        return KeywordResponse.builder()
                .id(savedKeyword.getId())
                .keyword(savedKeyword.getKeyword())
                .isActive(savedKeyword.getIsActive())
                .createdAt(savedKeyword.getCreatedAt())
                .updatedAt(savedKeyword.getUpdatedAt())
                .build();
    }
    
    @Transactional(readOnly = true)
    public List<KeywordResponse> getUserKeywords(Long userId) {
        log.info("사용자 키워드 목록 조회: 사용자={}", userId);
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        List<Keyword> keywords = keywordRepository.findByUserOrderByCreatedAtDesc(user);
        
        return keywords.stream()
                .map(keyword -> KeywordResponse.builder()
                        .id(keyword.getId())
                        .keyword(keyword.getKeyword())
                        .isActive(keyword.getIsActive())
                        .createdAt(keyword.getCreatedAt())
                        .updatedAt(keyword.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<KeywordResponse> getUserActiveKeywords(Long userId) {
        log.info("사용자 활성 키워드 목록 조회: 사용자={}", userId);
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        List<Keyword> keywords = keywordRepository.findByUserAndIsActiveTrueOrderByCreatedAtDesc(user);
        
        return keywords.stream()
                .map(keyword -> KeywordResponse.builder()
                        .id(keyword.getId())
                        .keyword(keyword.getKeyword())
                        .isActive(keyword.getIsActive())
                        .createdAt(keyword.getCreatedAt())
                        .updatedAt(keyword.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public KeywordResponse getKeywordById(Long keywordId) {
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new KeywordNotFoundException("키워드를 찾을 수 없습니다: " + keywordId));
        
        return KeywordResponse.builder()
                .id(keyword.getId())
                .keyword(keyword.getKeyword())
                .isActive(keyword.getIsActive())
                .createdAt(keyword.getCreatedAt())
                .updatedAt(keyword.getUpdatedAt())
                .build();
    }
    
    public KeywordResponse updateKeywordStatus(Long keywordId, boolean isActive) {
        log.info("키워드 상태 변경 요청: ID={}, isActive={}", keywordId, isActive);
        
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new KeywordNotFoundException("키워드를 찾을 수 없습니다: " + keywordId));
        
        keyword.setIsActive(isActive);
        Keyword updatedKeyword = keywordRepository.save(keyword);
        
        log.info("키워드 상태 변경 완료: {} -> {}", keyword.getKeyword(), isActive);
        
        return KeywordResponse.builder()
                .id(updatedKeyword.getId())
                .keyword(updatedKeyword.getKeyword())
                .isActive(updatedKeyword.getIsActive())
                .createdAt(updatedKeyword.getCreatedAt())
                .updatedAt(updatedKeyword.getUpdatedAt())
                .build();
    }
    
    public void deleteKeyword(Long keywordId) {
        log.info("키워드 삭제 요청: ID={}", keywordId);
        
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new KeywordNotFoundException("키워드를 찾을 수 없습니다: " + keywordId));
        
        keywordRepository.delete(keyword);
        log.info("키워드 삭제 완료: {}", keyword.getKeyword());
    }
} 