-- 기본 테스트 데이터 삽입
-- 애플리케이션 시작 시 자동으로 실행됩니다

-- 1. 기본 사용자 생성 (비밀번호: password123)
INSERT INTO users (username, email, password, created_at) 
VALUES ('testuser', 'test@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', CURRENT_TIMESTAMP);

-- 2. 기본 뉴스 사이트 생성
INSERT INTO news_sites (name, base_url, search_url_pattern, is_active, created_at) 
VALUES 
    ('네이버 뉴스', 'https://search.naver.com', 'https://search.naver.com/search.naver?query={keyword}&where=news', true, CURRENT_TIMESTAMP),
    ('구글 뉴스', 'https://news.google.com', 'https://news.google.com/rss/search?q={keyword}&hl=ko&gl=KR&ceid=KR:ko', true, CURRENT_TIMESTAMP);

-- 3. 기본 키워드 생성 (testuser의 키워드)
INSERT INTO keywords (keyword, user_id, is_active, created_at, updated_at) 
VALUES 
    ('AI', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('인공지능', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('머신러닝', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 