package com.melllon.newsdatacollectservice.repository;

import com.melllon.newsdatacollectservice.entity.Keyword;
import com.melllon.newsdatacollectservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    
    List<Keyword> findByIsActiveTrue();
    
    boolean existsByUserAndKeyword(User user, String keyword);
    
    List<Keyword> findByUserOrderByCreatedAtDesc(User user);
    
    List<Keyword> findByUserAndIsActiveTrueOrderByCreatedAtDesc(User user);
} 