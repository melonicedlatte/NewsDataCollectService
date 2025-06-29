package com.melllon.newsdatacollectservice.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 비밀번호 해시 생성을 위한 유틸리티 클래스
 * data.sql에서 사용할 해시된 비밀번호를 생성하는 용도
 */
public class PasswordHashUtil {
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    public static void main(String[] args) {
        // data.sql에서 사용할 비밀번호 해시 생성
        String password = "password123";
        String hashedPassword = encoder.encode(password);
        
        System.out.println("원본 비밀번호: " + password);
        System.out.println("해시된 비밀번호: " + hashedPassword);
        System.out.println();
        System.out.println("data.sql에서 사용할 INSERT 문:");
        System.out.println("INSERT INTO users (username, email, password, created_at) VALUES ('testuser', 'test@example.com', '" + hashedPassword + "', CURRENT_TIMESTAMP);");
    }
    
    public static String hashPassword(String password) {
        return encoder.encode(password);
    }
} 