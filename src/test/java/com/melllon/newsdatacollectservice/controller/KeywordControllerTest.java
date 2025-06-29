package com.melllon.newsdatacollectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melllon.newsdatacollectservice.dto.request.KeywordRequest;
import com.melllon.newsdatacollectservice.dto.request.UserRegisterRequest;
import com.melllon.newsdatacollectservice.dto.request.UserLoginRequest;
import com.melllon.newsdatacollectservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class KeywordControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private com.melllon.newsdatacollectservice.service.NewsCollectionService newsCollectionService;

    private MockMvc mockMvc;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 사용자 등록 및 로그인하여 토큰 획득
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        String loginResponse = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // JSON에서 토큰 추출 (간단한 방식)
        authToken = loginResponse.split("\"token\":\"")[1].split("\"")[0];
    }

    @Test
    void createKeyword_Success() throws Exception {
        // given
        Long userId = createTestUserAndGetId();
        KeywordRequest request = new KeywordRequest("AI");

        // when & then
        mockMvc.perform(post("/api/keywords?userId=" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.keyword").value("AI"));
    }

    @Test
    void createKeyword_DuplicateKeyword() throws Exception {
        // given
        Long userId = createTestUserAndGetId();
        KeywordRequest request = new KeywordRequest("AI");
        // 최초 등록
        mockMvc.perform(post("/api/keywords?userId=" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        // 중복 등록
        mockMvc.perform(post("/api/keywords?userId=" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 존재하는 키워드입니다: AI"));
    }

    @Test
    void getKeywords_Success() throws Exception {
        // given
        Long userId = createTestUserAndGetId();
        KeywordRequest request = new KeywordRequest("AI");
        mockMvc.perform(post("/api/keywords?userId=" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // when & then
        mockMvc.perform(get("/api/keywords/user/" + userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void createKeyword_Unauthorized() throws Exception {
        // given
        KeywordRequest request = new KeywordRequest();
        request.setKeyword("AI");

        // when & then
        mockMvc.perform(post("/api/keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // 테스트용 임시 유저 생성 메서드
    private Long createTestUserAndGetId() {
        return userRepository.findByUsername("testuser")
                .map(com.melllon.newsdatacollectservice.entity.User::getId)
                .orElseGet(() -> {
                    com.melllon.newsdatacollectservice.entity.User user = com.melllon.newsdatacollectservice.entity.User.builder()
                            .username("testuser")
                            .email("test@example.com")
                            .password("password")
                            .build();
                    return userRepository.saveAndFlush(user).getId();
                });
    }
} 