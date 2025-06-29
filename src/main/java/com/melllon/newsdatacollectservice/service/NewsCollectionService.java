package com.melllon.newsdatacollectservice.service;

import com.melllon.newsdatacollectservice.entity.Keyword;
import com.melllon.newsdatacollectservice.entity.NewsArticle;
import com.melllon.newsdatacollectservice.entity.NewsSite;
import com.melllon.newsdatacollectservice.repository.KeywordRepository;
import com.melllon.newsdatacollectservice.repository.NewsArticleRepository;
import com.melllon.newsdatacollectservice.repository.NewsSiteRepository;
import com.melllon.newsdatacollectservice.service.crawler.NewsCrawler;
import com.melllon.newsdatacollectservice.service.crawler.NewsCrawlerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NewsCollectionService {
    
    private final NewsSiteRepository newsSiteRepository;
    private final KeywordRepository keywordRepository;
    private final NewsArticleRepository newsArticleRepository;
    private final NewsCrawlerFactory crawlerFactory;
    
    // 병렬 처리를 위한 스레드 풀
    private final Executor executor = Executors.newFixedThreadPool(10);
    
    /**
     * 모든 활성 키워드에 대해 뉴스를 수집합니다.
     */
    public void collectNewsForAllActiveKeywords() {
        log.info("전체 활성 키워드에 대한 뉴스 수집 시작");
        
        List<Keyword> activeKeywords = keywordRepository.findByIsActiveTrue();
        List<NewsSite> activeNewsSites = newsSiteRepository.findByIsActiveTrue();
        
        if (activeKeywords.isEmpty()) {
            log.info("활성 키워드가 없습니다.");
            return;
        }
        
        if (activeNewsSites.isEmpty()) {
            log.info("활성 뉴스 사이트가 없습니다.");
            return;
        }
        
        // 각 키워드에 대해 병렬로 뉴스 수집
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (Keyword keyword : activeKeywords) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    collectNewsForKeyword(keyword, activeNewsSites);
                } catch (Exception e) {
                    log.error("키워드 '{}'에 대한 뉴스 수집 중 오류 발생: {}", keyword.getKeyword(), e.getMessage());
                }
            }, executor);
            
            futures.add(future);
        }
        
        // 모든 작업 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        log.info("전체 활성 키워드에 대한 뉴스 수집 완료");
    }
    
    /**
     * 특정 키워드에 대해 뉴스를 수집합니다.
     */
    @Async
    public void collectNewsForKeyword(Keyword keyword, List<NewsSite> newsSites) {
        log.info("키워드 '{}'에 대한 뉴스 수집 시작", keyword.getKeyword());
        
        for (NewsSite newsSite : newsSites) {
            try {
                collectNewsFromSite(keyword, newsSite);
            } catch (Exception e) {
                log.error("뉴스 사이트 '{}'에서 키워드 '{}' 수집 중 오류: {}", 
                         newsSite.getName(), keyword.getKeyword(), e.getMessage());
            }
        }
        
        log.info("키워드 '{}'에 대한 뉴스 수집 완료", keyword.getKeyword());
    }
    
    /**
     * 특정 뉴스 사이트에서 키워드에 대한 뉴스를 수집합니다.
     */
    private void collectNewsFromSite(Keyword keyword, NewsSite newsSite) {
        try {
            // 검색 URL 생성
            String searchUrl = buildSearchUrl(newsSite, keyword.getKeyword());
            
            log.info("뉴스 사이트 '{}'에서 키워드 '{}' 수집 중: {}", 
                     newsSite.getName(), keyword.getKeyword(), searchUrl);
            
            // 웹 페이지 크롤링
            log.info("웹 페이지 크롤링 시작: {}", searchUrl);
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(15000)
                    .get();
            
            log.info("웹 페이지 크롤링 완료. 페이지 제목: {}", doc.title());
            log.info("페이지 HTML 길이: {} 문자", doc.html().length());
            
            // 적절한 크롤러 선택 및 뉴스 기사 추출
            NewsCrawler crawler = crawlerFactory.getCrawler(newsSite);
            List<NewsArticle> articles = crawler.extractNewsArticles(doc, newsSite, keyword);
            
            log.info("키워드 '{}'에서 {}개의 뉴스 기사를 찾았습니다.", keyword.getKeyword(), articles.size());
            
            // 중복 제거 및 저장
            int savedCount = 0;
            for (NewsArticle article : articles) {
                if (!newsArticleRepository.existsByUrl(article.getUrl())) {
                    newsArticleRepository.save(article);
                    savedCount++;
                    log.info("새로운 뉴스 기사 저장: {}", article.getTitle());
                } else {
                    log.info("중복 뉴스 기사 건너뛰기: {}", article.getTitle());
                }
            }
            
            log.info("키워드 '{}'에서 {}개의 새로운 뉴스 기사를 저장했습니다.", keyword.getKeyword(), savedCount);
            
        } catch (IOException e) {
            log.error("뉴스 사이트 '{}' 크롤링 중 오류: {}", newsSite.getName(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("뉴스 사이트 '{}' 처리 중 예상치 못한 오류: {}", newsSite.getName(), e.getMessage(), e);
        }
    }
    
    /**
     * 검색 URL을 생성합니다.
     */
    private String buildSearchUrl(NewsSite newsSite, String keyword) {
        // 실제 구현에서는 각 뉴스 사이트별 검색 URL 패턴을 적용
        String searchUrl = newsSite.getSearchUrlPattern();
        
        // 간단한 예시 (실제로는 각 사이트별로 다르게 구현)
        if (searchUrl.contains("{keyword}")) {
            searchUrl = searchUrl.replace("{keyword}", keyword);
        } else {
            // 기본 검색 URL 생성
            searchUrl = newsSite.getBaseUrl() + "/search?q=" + keyword;
        }
        
        return searchUrl;
    }
} 