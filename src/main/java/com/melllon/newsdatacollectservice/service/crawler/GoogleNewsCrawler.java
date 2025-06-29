package com.melllon.newsdatacollectservice.service.crawler;

import com.melllon.newsdatacollectservice.entity.Keyword;
import com.melllon.newsdatacollectservice.entity.NewsArticle;
import com.melllon.newsdatacollectservice.entity.NewsSite;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GoogleNewsCrawler implements NewsCrawler {
    
    @Autowired
    private SeleniumGoogleNewsContentFetcher seleniumFetcher;
    
    @Override
    public boolean canHandle(NewsSite newsSite) {
        return "구글 뉴스".equals(newsSite.getName());
    }
    
    @Override
    public List<NewsArticle> extractNewsArticles(Document doc, NewsSite newsSite, Keyword keyword) {
        List<NewsArticle> articles = new ArrayList<>();
        
        try {
            log.info("구글 뉴스 RSS 피드 크롤링 시작");
            
            // RSS 피드에서 item 요소들 찾기
            Elements items = doc.select("item");
            log.info("구글 뉴스 RSS에서 {}개의 기사를 찾았습니다", items.size());
            
            // 테스트를 위해 10개로 제한
            int maxArticles = 10;
            int processedCount = 0;
            
            for (Element item : items) {
                if (processedCount >= maxArticles) {
                    log.info("테스트를 위해 {}개 기사로 제한하여 크롤링을 중단합니다", maxArticles);
                    break;
                }
                
                try {
                    // RSS에서 기본 정보 추출
                    String title = item.select("title").text();
                    String link = item.select("link").text();
                    String pubDate = item.select("pubDate").text();
                    
                    if (title.isEmpty() || link.isEmpty()) {
                        continue;
                    }
                    
                    log.info("구글 뉴스 RSS 기사 처리: {}", title);
                    
                    // 셀레니움으로 실제 기사 URL 추출 및 본문 크롤링
                    String content = crawlContentWithSelenium(link);
                    
                    // 셀레니움 크롤링 실패 시 기본 내용 사용
                    if (content == null || content.isEmpty()) {
                        content = "구글 뉴스에서 수집된 기사입니다. 자세한 내용은 링크를 참조하세요.";
                    }
                    
                    // 발행일 파싱
                    LocalDateTime publishedDate = parseRssDate(pubDate);
                    
                    NewsArticle article = NewsArticle.builder()
                            .title(title)
                            .content(content)
                            .url(link)
                            .publishedAt(publishedDate)
                            .newsSite(newsSite)
                            .keyword(keyword)
                            .collectedAt(LocalDateTime.now())
                            .build();
                    
                    articles.add(article);
                    processedCount++;
                    
                    log.info("구글 뉴스 RSS 기사 추출 성공: {} (내용 길이: {} 문자)", title, content.length());
                    
                } catch (Exception e) {
                    log.warn("구글 뉴스 RSS 기사 처리 중 오류: {}", e.getMessage());
                }
            }
            
            log.info("구글 뉴스 크롤링 완료: {}개의 기사 수집 (제한: {}개)", articles.size(), maxArticles);
            
        } catch (Exception e) {
            log.error("구글 뉴스 RSS 피드 처리 중 오류: {}", e.getMessage(), e);
        }
        
        return articles;
    }
    
    @Override
    public String crawlFullContent(String url, String siteName) {
        return crawlContentWithSelenium(url);
    }
    
    /**
     * 셀레니움을 사용하여 구글 뉴스 리다이렉트 URL에서 실제 뉴스 본문을 크롤링합니다.
     */
    private String crawlContentWithSelenium(String googleNewsUrl) {
        try {
            log.info("구글 뉴스 셀레니움 크롤링 시작: {}", googleNewsUrl);
            
            // 셀레니움으로 실제 기사 URL 추출 및 본문 크롤링
            String content = seleniumFetcher.crawlContentWithSelenium(googleNewsUrl);
            
            if (content != null && !content.trim().isEmpty()) {
                log.info("구글 뉴스 셀레니움 크롤링 성공: {} 문자", content.length());
                return content;
            } else {
                log.warn("구글 뉴스 셀레니움 크롤링에서 내용을 추출할 수 없습니다: {}", googleNewsUrl);
                return null;
            }
            
        } catch (Exception e) {
            log.error("구글 뉴스 셀레니움 크롤링 실패: {} - {}", googleNewsUrl, e.getMessage());
            return null;
        }
    }
    
    /**
     * RSS 날짜 파싱
     */
    private LocalDateTime parseRssDate(String dateStr) {
        try {
            // 다양한 RSS 날짜 형식 처리
            String[] patterns = {
                "EEE, dd MMM yyyy HH:mm:ss Z",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "EEE MMM dd HH:mm:ss zzz yyyy"
            };
            
            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    return LocalDateTime.parse(dateStr, formatter);
                } catch (Exception e) {
                    // 다음 패턴 시도
                    continue;
                }
            }
            
            log.warn("구글 뉴스 RSS 날짜 파싱 실패: {}", dateStr);
            return LocalDateTime.now();
            
        } catch (Exception e) {
            log.warn("구글 뉴스 RSS 날짜 파싱 실패: {}", dateStr);
            return LocalDateTime.now();
        }
    }
} 