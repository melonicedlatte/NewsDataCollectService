package com.melllon.newsdatacollectservice.service.crawler;

import com.melllon.newsdatacollectservice.entity.Keyword;
import com.melllon.newsdatacollectservice.entity.NewsArticle;
import com.melllon.newsdatacollectservice.entity.NewsSite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class NaverNewsCrawlerTest {

    private NaverNewsCrawler naverNewsCrawler;
    private NewsSite naverNewsSite;
    private Keyword testKeyword;

    @BeforeEach
    void setUp() {
        naverNewsCrawler = new NaverNewsCrawler();
        
        naverNewsSite = NewsSite.builder()
                .id(1L)
                .name("네이버 뉴스")
                .baseUrl("https://search.naver.com")
                .searchUrlPattern("https://search.naver.com/search.naver?query={keyword}&where=news")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        testKeyword = Keyword.builder()
                .id(1L)
                .keyword("AI")
                .build();
    }

    @Test
    void canHandle_NaverNewsSite_ReturnsTrue() {
        // when
        boolean result = naverNewsCrawler.canHandle(naverNewsSite);

        // then
        assertTrue(result);
    }

    @Test
    void canHandle_NonNaverSite_ReturnsFalse() {
        // given
        NewsSite googleNewsSite = NewsSite.builder()
                .name("구글 뉴스")
                .baseUrl("https://news.google.com")
                .searchUrlPattern("https://news.google.com/rss/search?q={keyword}&hl=ko&gl=KR")
                .build();

        // when
        boolean result = naverNewsCrawler.canHandle(googleNewsSite);

        // then
        assertFalse(result);
    }

    @Test
    void canHandle_EmptySiteName_ReturnsFalse() {
        // given
        NewsSite emptySite = NewsSite.builder()
                .name("")
                .baseUrl("https://search.naver.com")
                .build();

        // when
        boolean result = naverNewsCrawler.canHandle(emptySite);

        // then
        assertFalse(result);
    }
} 