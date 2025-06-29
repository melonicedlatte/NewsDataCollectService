package com.melllon.newsdatacollectservice.scheduler;

import com.melllon.newsdatacollectservice.service.NewsCollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsCollectionScheduler {
    
    private final NewsCollectionService newsCollectionService;
    
    /**
     * 10분마다 뉴스 수집 실행
     */
    @Scheduled(fixedRate = 600000) // 10분 = 600,000ms
    public void scheduledNewsCollection() {
        log.info("스케줄된 뉴스 수집 작업 시작");
        
        try {
            newsCollectionService.collectNewsForAllActiveKeywords();
            log.info("스케줄된 뉴스 수집 작업 완료");
        } catch (Exception e) {
            log.error("스케줄된 뉴스 수집 작업 중 오류 발생: {}", e.getMessage(), e);
        }
    }
} 