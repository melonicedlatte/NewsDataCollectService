package com.melllon.newsdatacollectservice.service.crawler;

import com.melllon.newsdatacollectservice.entity.Keyword;
import com.melllon.newsdatacollectservice.entity.NewsArticle;
import com.melllon.newsdatacollectservice.entity.NewsSite;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class NaverNewsCrawler implements NewsCrawler {
    
    @Override
    public boolean canHandle(NewsSite newsSite) {
        return "네이버 뉴스".equals(newsSite.getName());
    }
    
    @Override
    public List<NewsArticle> extractNewsArticles(Document doc, NewsSite newsSite, Keyword keyword) {
        List<NewsArticle> articles = new ArrayList<>();
        
        try {
            // 새로운 네이버 뉴스 HTML 구조에 맞는 셀렉터들
            String[] selectors = {
                "div.sds-comps-vertical-layout.sds-comps-full-layout.I6obO60yNcW8I32mDzvQ",
                "div.news_wrap.api_ani_send",
                "div.news_area",
                "div.news_box",
                "div.sds-comps-vertical-layout.sds-comps-full-layout", // 새로운 구조
                "div[class*='sds-comps-vertical-layout']", // 클래스명에 vertical-layout이 포함된 요소
                "div[class*='news']", // 클래스명에 news가 포함된 요소
                "div.api_subject_bx" // API 주제 박스
            };
            
            Elements newsElements = null;
            String usedSelector = null;
            
            for (String selector : selectors) {
                newsElements = doc.select(selector);
                if (!newsElements.isEmpty()) {
                    usedSelector = selector;
                    log.info("네이버 뉴스에서 셀렉터 '{}'로 {}개의 기사 요소를 찾았습니다.", 
                             usedSelector, newsElements.size());
                    break;
                }
            }
            
            if (newsElements != null && !newsElements.isEmpty()) {
                // 테스트를 위해 10개로 제한
                int maxArticles = 10;
                int processedCount = 0;
                
                for (Element element : newsElements) {
                    if (processedCount >= maxArticles) {
                        log.info("테스트를 위해 {}개 기사로 제한하여 크롤링을 중단합니다", maxArticles);
                        break;
                    }
                    
                    try {
                        NewsArticle article = extractNaverArticleFromElement(element, newsSite, keyword);
                        if (article != null) {
                            articles.add(article);
                            processedCount++;
                        }
                    } catch (Exception e) {
                        log.warn("네이버 뉴스 기사 추출 중 오류: {}", e.getMessage());
                    }
                }
                
                log.info("네이버 뉴스 크롤링 완료: {}개의 기사 수집 (제한: {}개)", articles.size(), maxArticles);
            } else {
                log.warn("네이버 뉴스에서 기사 요소를 찾을 수 없습니다. 사용 가능한 셀렉터들을 모두 시도했습니다.");
            }
            
        } catch (Exception e) {
            log.error("네이버 뉴스 기사 추출 중 오류: {}", e.getMessage(), e);
        }
        
        return articles;
    }
    
    @Override
    public String crawlFullContent(String url, String siteName) {
        try {
            log.info("네이버 뉴스 개별 페이지 크롤링 시작: {}", url);
            
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();
            
            log.info("네이버 뉴스 페이지 로드 완료. 제목: {}", doc.title());
            
            // 네이버 뉴스 본문 셀렉터들 (우선순위 순)
            String[] contentSelectors = {
                "div#contents > div#newsct_article > article#dic_area",
                "div#contents div#newsct_article article#dic_area",
                "div#contents div#newsct_article",
                "div#contents article#dic_area",
                "div#contents",
                "article#dic_area",
                "div.article_body",
                "div.article-content"
            };
            
            for (String selector : contentSelectors) {
                Element contentElement = doc.selectFirst(selector);
                if (contentElement != null) {
                    // 불필요한 요소들 제거
                    contentElement.select("script, style, .advertisement, .ad, .banner, .reporter_area, .copyright, .link_news").remove();
                    
                    String content = contentElement.text().trim();
                    if (!content.isEmpty() && content.length() > 100) {
                        log.info("네이버 뉴스 본문 요소 찾음: {}", selector);
                        log.info("네이버 뉴스 본문 크롤링 성공: {} 문자", content.length());
                        return content;
                    }
                }
            }
            
            // 메타 태그에서 설명 가져오기 (대안)
            Element metaDesc = doc.selectFirst("meta[name=description]");
            if (metaDesc != null) {
                String description = metaDesc.attr("content").trim();
                if (!description.isEmpty() && description.length() > 50) {
                    log.info("네이버 뉴스 메타 설명 사용: {} 문자", description.length());
                    return description;
                }
            }
            
            log.warn("네이버 뉴스 본문을 찾을 수 없습니다: {}", url);
            return null;
            
        } catch (Exception e) {
            log.error("네이버 뉴스 개별 페이지 크롤링 실패: {} - {}", url, e.getMessage());
            return null;
        }
    }
    
    private NewsArticle extractNaverArticleFromElement(Element element, NewsSite newsSite, Keyword keyword) {
        try {
            // 네이버 뉴스 링크 추출 (우선적으로 span 내 a 태그에서 찾기)
            String newsUrl = extractNaverNewsUrl(element);
            if (newsUrl == null || newsUrl.isEmpty()) {
                return null;
            }
            
            log.info("네이버 뉴스 링크 찾음: {}", newsUrl);
            
            // 제목 추출
            String title = extractNaverTitle(element);
            if (title == null || title.isEmpty()) {
                return null;
            }
            
            // 개별 페이지에서 전체 내용 크롤링
            String content = crawlFullContent(newsUrl, newsSite.getName());
            if (content == null || content.isEmpty()) {
                // 개별 페이지 크롤링 실패 시 현재 페이지에서 추출
                content = extractNaverContent(element);
            }
            
            // 발행일 추출
            LocalDateTime publishedDate = extractNaverPublishedDate(element);
            
            return NewsArticle.builder()
                    .title(title)
                    .content(content)
                    .url(newsUrl)
                    .publishedAt(publishedDate)
                    .newsSite(newsSite)
                    .keyword(keyword)
                    .collectedAt(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.warn("네이버 뉴스 기사 추출 중 오류: {}", e.getMessage());
            return null;
        }
    }
    
    private String extractNaverNewsUrl(Element element) {
        // 네이버 뉴스 링크를 우선적으로 찾기 (span 내 a 태그)
        Elements spanLinks = element.select("span a[href*='n.news.naver.com']");
        if (!spanLinks.isEmpty()) {
            return spanLinks.first().attr("href");
        }
        
        // 일반적인 a 태그에서 네이버 뉴스 링크 찾기
        Elements links = element.select("a[href*='n.news.naver.com']");
        if (!links.isEmpty()) {
            return links.first().attr("href");
        }
        
        // 다른 뉴스 링크들도 확인
        Elements allLinks = element.select("a[href]");
        for (Element link : allLinks) {
            String href = link.attr("href");
            if (href.contains("news.naver.com") || href.contains("n.news.naver.com")) {
                return href;
            }
        }
        
        return null;
    }
    
    private String extractNaverTitle(Element element) {
        // 제목 추출을 위한 다양한 셀렉터 시도
        String[] titleSelectors = {
            "span.sds-comps-text-type-headline1",
            "span.sds-comps-text-ellipsis-1.sds-comps-text-type-headline1",
            "a span.sds-comps-text-type-headline1",
            "a span.sds-comps-text-ellipsis-1",
            "span.sds-comps-text-ellipsis-1",
            "a.news_tit",
            "a[href*='n.news.naver.com']",
            "a[href*='news.naver.com']",
            "a.news_title",
            "a.title",
            "h3 a",
            "h2 a",
            "a"
        };
        
        for (String selector : titleSelectors) {
            Element titleElement = element.selectFirst(selector);
            if (titleElement != null) {
                String title = titleElement.text().trim();
                log.debug("제목 셀렉터 '{}'로 제목 찾음: {}", selector, title);
                if (!title.isEmpty() && title.length() > 5) {
                    log.info("네이버 뉴스 제목 추출 성공: {}", title);
                    return title;
                }
            } else {
                log.debug("제목 셀렉터 '{}'에서 요소를 찾을 수 없음", selector);
            }
        }
        
        log.warn("네이버 뉴스 제목을 찾을 수 없습니다.");
        return null;
    }
    
    private String extractNaverContent(Element element) {
        // 간단한 내용 추출 (개별 페이지 크롤링이 실패했을 때 사용)
        String[] contentSelectors = {
            "div.news_dsc",
            "div.news_summary",
            "div.summary",
            "p.news_dsc",
            "p.summary"
        };
        
        for (String selector : contentSelectors) {
            Element contentElement = element.selectFirst(selector);
            if (contentElement != null) {
                String content = contentElement.text().trim();
                if (!content.isEmpty() && content.length() > 10) {
                    return content;
                }
            }
        }
        
        return "내용을 확인하려면 링크를 클릭하세요.";
    }
    
    private LocalDateTime extractNaverPublishedDate(Element element) {
        // 발행일 추출
        String[] dateSelectors = {
            "span.info",
            "span.date",
            "span.time",
            "div.info",
            "div.date"
        };
        
        for (String selector : dateSelectors) {
            Element dateElement = element.selectFirst(selector);
            if (dateElement != null) {
                String dateText = dateElement.text().trim();
                LocalDateTime parsedDate = parseNaverDate(dateText);
                if (parsedDate != null) {
                    return parsedDate;
                }
            }
        }
        
        return LocalDateTime.now();
    }
    
    private LocalDateTime parseNaverDate(String dateText) {
        try {
            // 네이버 뉴스의 다양한 날짜 형식 처리
            if (dateText.contains("분 전")) {
                return LocalDateTime.now();
            } else if (dateText.contains("시간 전")) {
                return LocalDateTime.now();
            } else if (dateText.contains("일 전")) {
                return LocalDateTime.now();
            } else if (dateText.matches("\\d{4}\\.\\d{1,2}\\.\\d{1,2}")) {
                return LocalDateTime.parse(dateText + " 00:00", 
                    DateTimeFormatter.ofPattern("yyyy.M.d HH:mm"));
            } else if (dateText.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                return LocalDateTime.parse(dateText + " 00:00", 
                    DateTimeFormatter.ofPattern("yyyy-M-d HH:mm"));
            }
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateText);
        }
        
        return LocalDateTime.now();
    }
} 