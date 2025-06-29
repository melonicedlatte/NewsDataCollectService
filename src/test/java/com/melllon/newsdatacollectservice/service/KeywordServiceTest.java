package com.melllon.newsdatacollectservice.service;

import com.melllon.newsdatacollectservice.dto.request.KeywordRequest;
import com.melllon.newsdatacollectservice.dto.response.KeywordResponse;
import com.melllon.newsdatacollectservice.entity.Keyword;
import com.melllon.newsdatacollectservice.entity.User;
import com.melllon.newsdatacollectservice.exception.KeywordAlreadyExistsException;
import com.melllon.newsdatacollectservice.exception.KeywordNotFoundException;
import com.melllon.newsdatacollectservice.repository.KeywordRepository;
import com.melllon.newsdatacollectservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeywordServiceTest {

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private KeywordService keywordService;

    private User testUser;
    private Keyword testKeyword;
    private KeywordRequest keywordRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();

        testKeyword = Keyword.builder()
                .id(1L)
                .keyword("AI")
                .user(testUser)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        keywordRequest = new KeywordRequest();
        keywordRequest.setKeyword("AI");
    }

    @Test
    void createKeyword_Success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(keywordRepository.existsByUserAndKeyword(testUser, "AI")).thenReturn(false);
        when(keywordRepository.save(any(Keyword.class))).thenReturn(testKeyword);

        // when
        KeywordResponse result = keywordService.createKeyword(1L, keywordRequest);

        // then
        assertNotNull(result);
        assertEquals("AI", result.getKeyword());
        assertTrue(result.getIsActive());
        verify(userRepository).findById(1L);
        verify(keywordRepository).existsByUserAndKeyword(testUser, "AI");
        verify(keywordRepository).save(any(Keyword.class));
    }

    @Test
    void createKeyword_UserNotFound() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> {
            keywordService.createKeyword(1L, keywordRequest);
        });
        verify(userRepository).findById(1L);
        verify(keywordRepository, never()).save(any(Keyword.class));
    }

    @Test
    void createKeyword_KeywordAlreadyExists() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(keywordRepository.existsByUserAndKeyword(testUser, "AI")).thenReturn(true);

        // when & then
        assertThrows(KeywordAlreadyExistsException.class, () -> {
            keywordService.createKeyword(1L, keywordRequest);
        });
        verify(userRepository).findById(1L);
        verify(keywordRepository).existsByUserAndKeyword(testUser, "AI");
        verify(keywordRepository, never()).save(any(Keyword.class));
    }

    @Test
    void getUserKeywords_Success() {
        // given
        List<Keyword> keywords = Arrays.asList(testKeyword);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(keywordRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(keywords);

        // when
        List<KeywordResponse> result = keywordService.getUserKeywords(1L);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("AI", result.get(0).getKeyword());
        verify(userRepository).findById(1L);
        verify(keywordRepository).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    void deleteKeyword_Success() {
        // given
        when(keywordRepository.findById(1L)).thenReturn(Optional.of(testKeyword));
        doNothing().when(keywordRepository).delete(testKeyword);

        // when
        keywordService.deleteKeyword(1L);

        // then
        verify(keywordRepository).findById(1L);
        verify(keywordRepository).delete(testKeyword);
    }

    @Test
    void deleteKeyword_KeywordNotFound() {
        // given
        when(keywordRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(KeywordNotFoundException.class, () -> {
            keywordService.deleteKeyword(1L);
        });
        verify(keywordRepository).findById(1L);
        verify(keywordRepository, never()).delete(any(Keyword.class));
    }
} 