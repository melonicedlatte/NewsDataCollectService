-- 테스트용 기본 사용자 생성
INSERT INTO users (username, email, password, created_at) 
VALUES ('testuser', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', CURRENT_TIMESTAMP);

-- 테스트용 키워드 생성
INSERT INTO keywords (keyword, user_id, created_at) 
VALUES ('AI', 1, CURRENT_TIMESTAMP);

-- 테스트용 뉴스 사이트 생성
INSERT INTO news_sites (name, base_url, search_url_pattern, is_active, created_at) 
VALUES 
    ('네이버 뉴스', 'https://search.naver.com', 'https://search.naver.com/search.naver?query={keyword}&where=news', true, CURRENT_TIMESTAMP),
    ('구글 뉴스', 'https://news.google.com', 'https://news.google.com/rss/search?q={keyword}&hl=ko&gl=KR&ceid=KR:ko', true, CURRENT_TIMESTAMP); 