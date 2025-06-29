package com.melllon.newsdatacollectservice.service.crawler;

import com.melllon.newsdatacollectservice.entity.NewsSite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsCrawlerFactory {
    
    private final List<NewsCrawler> crawlers;
    
    @PostConstruct
    public void init() {
        log.info("등록된 크롤러 목록:");
        for (NewsCrawler crawler : crawlers) {
            log.info("- {}", crawler.getClass().getSimpleName());
        }
    }
    
    /**
     * 뉴스 사이트에 적합한 크롤러를 반환합니다.
     */
    public NewsCrawler getCrawler(NewsSite newsSite) {
        log.info("뉴스 사이트 '{}'에 대한 크롤러 선택 시작", newsSite.getName());
        
        // 등록된 크롤러들 중에서 적합한 크롤러 찾기
        for (NewsCrawler crawler : crawlers) {
            log.info("크롤러 {} 확인 중...", crawler.getClass().getSimpleName());
            if (crawler.canHandle(newsSite)) {
                log.info("뉴스 사이트 '{}'에 대한 크롤러 선택: {}", 
                         newsSite.getName(), crawler.getClass().getSimpleName());
                return crawler;
            }
        }
        
        // 적합한 크롤러를 찾지 못한 경우
        log.error("뉴스 사이트 '{}'에 대한 적합한 크롤러를 찾을 수 없습니다.", newsSite.getName());
        throw new RuntimeException("뉴스 사이트 '" + newsSite.getName() + "'에 대한 적합한 크롤러를 찾을 수 없습니다.");
    }
} 