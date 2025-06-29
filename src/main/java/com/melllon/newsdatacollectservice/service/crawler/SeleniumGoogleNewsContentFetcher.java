package com.melllon.newsdatacollectservice.service.crawler;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

@Slf4j
@Component
public class SeleniumGoogleNewsContentFetcher {

    private static final int PAGE_LOAD_TIMEOUT = 30;
    
    /**
     * 셀레니움을 사용하여 구글 뉴스 리다이렉트 URL에서 실제 뉴스 본문을 크롤링합니다.
     */
    public String crawlContentWithSelenium(String googleNewsUrl) {
        WebDriver driver = null;
        try {
            log.info("[셀레니움] 1. 최초 접근한 URL: {}", googleNewsUrl);
            driver = setupWebDriver();
            driver.get(googleNewsUrl);
            log.info("[셀레니움] 2. 현재 페이지 타이틀: {}", driver.getTitle());
            log.info("[셀레니움] 3. 현재 URL: {}", driver.getCurrentUrl());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(PAGE_LOAD_TIMEOUT));
            // news.google.com이 아닌 곳으로 리다이렉트될 때까지 대기 (최대 PAGE_LOAD_TIMEOUT초)
            boolean redirected = wait.until(d -> !d.getCurrentUrl().contains("news.google.com"));
            String currentUrl = driver.getCurrentUrl();
            log.info("[셀레니움] 4. 리다이렉트 후 URL: {}", currentUrl);

            if (redirected && !currentUrl.contains("news.google.com")) {
                // 실제 뉴스 기사 페이지에서 본문 추출
                String content = extractContentFromNewsPage(driver, wait, currentUrl);
                if (content != null && !content.trim().isEmpty()) {
                    log.info("[셀레니움] 5. 본문 추출 성공: {} 문자", content.length());
                    return content;
                } else {
                    log.warn("[셀레니움] 본문 추출 실패: {}", currentUrl);
                }
            } else {
                log.warn("[셀레니움] 리다이렉트가 감지되지 않음, 현재 URL: {}", currentUrl);
            }
            return null;
        } catch (Exception e) {
            log.error("[셀레니움] 크롤링 중 예외 발생: {}", e.getMessage(), e);
            return null;
        } finally {
            if (driver != null) {
                driver.quit();
                log.info("[셀레니움] WebDriver 종료 완료");
            }
        }
    }
    
    /**
     * 테스트를 위해 여러 URL을 처리하되 10개로 제한하는 메서드
     */
    public List<String> crawlMultipleUrlsWithLimit(List<String> urls) {
        List<String> results = new ArrayList<>();
        int maxUrls = 10; // 테스트를 위해 10개로 제한
        int processedCount = 0;
        
        log.info("셀레니움 기반 다중 URL 크롤링 시작 (제한: {}개)", maxUrls);
        
        for (String url : urls) {
            if (processedCount >= maxUrls) {
                log.info("테스트를 위해 {}개 URL로 제한하여 크롤링을 중단합니다", maxUrls);
                break;
            }
            
            try {
                String content = crawlContentWithSelenium(url);
                if (content != null && !content.trim().isEmpty()) {
                    results.add(content);
                    processedCount++;
                    log.info("URL {} 크롤링 성공 ({}번째)", url, processedCount);
                } else {
                    log.warn("URL {}에서 내용을 추출할 수 없습니다", url);
                }
            } catch (Exception e) {
                log.error("URL {} 크롤링 실패: {}", url, e.getMessage());
            }
        }
        
        log.info("셀레니움 다중 URL 크롤링 완료: {}개 성공 (제한: {}개)", results.size(), maxUrls);
        return results;
    }
    
    /**
     * WebDriver 설정
     */
    private WebDriver setupWebDriver() {
        // Chrome 드라이버 자동 설정
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 헤드리스 모드
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        
        return driver;
    }

    /**
     * 뉴스 페이지에서 본문 추출
     */
    private String extractContentFromNewsPage(WebDriver driver, WebDriverWait wait, String url) {
        try {
            // 페이지 로딩 대기
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            
            // 다양한 뉴스 사이트의 본문 셀렉터 시도
            String[] contentSelectors = {
                "article",
                ".article-content",
                ".article-body",
                ".news-content",
                ".news-body",
                ".content",
                ".post-content",
                ".entry-content",
                ".story-content",
                ".article-text",
                ".news-text",
                ".post-body",
                ".entry-body",
                ".story-body",
                "div[class*='content']",
                "div[class*='article']",
                "div[class*='story']",
                "div[class*='post']",
                "div[class*='entry']",
                ".article_view",
                ".news_view",
                ".content_view",
                ".post_view",
                ".story_view"
            };
            
            for (String selector : contentSelectors) {
                try {
                    List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                    for (WebElement element : elements) {
                        String text = element.getText().trim();
                        if (text.length() > 200) { // 충분한 길이의 텍스트인지 확인
                            log.info("본문 추출 성공 (셀렉터: {}): {} 문자", selector, text.length());
                            return text;
                        }
                    }
                } catch (Exception e) {
                    // 개별 셀렉터 실패는 무시하고 다음 시도
                    log.warn("셀렉터 {}에서 본문 추출 실패: {}", selector, e.getMessage());
                }
            }
            
            // 모든 셀렉터가 실패한 경우 body 전체에서 텍스트 추출
            WebElement body = driver.findElement(By.tagName("body"));
            String bodyText = body.getText().trim();
            
            if (bodyText.length() > 100) {
                log.info("body 전체에서 본문 추출: {} 문자", bodyText.length());
                return bodyText;
            }
            
            log.warn("본문을 찾을 수 없습니다: {}", url);
            return null;
            
        } catch (Exception e) {
            log.error("본문 추출 중 오류 발생: {} - {}", url, e.getMessage());
            return null;
        }
    }
} 