package com.melllon.newsdatacollectservice.service.crawler;

import com.melllon.newsdatacollectservice.entity.Keyword;
import com.melllon.newsdatacollectservice.entity.NewsArticle;
import com.melllon.newsdatacollectservice.entity.NewsSite;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 * 뉴스 크롤링을 위한 인터페이스
 */
public interface NewsCrawler {
    
    /**
     * 이 크롤러가 처리할 수 있는 뉴스 사이트인지 확인합니다.
     */
    boolean canHandle(NewsSite newsSite);
    
    /**
     * 웹 페이지에서 뉴스 기사들을 추출합니다.
     */
    List<NewsArticle> extractNewsArticles(Document doc, NewsSite newsSite, Keyword keyword);
    
    /**
     * 개별 뉴스 페이지에서 전체 내용을 크롤링합니다.
     */
    String crawlFullContent(String url, String siteName);
} 