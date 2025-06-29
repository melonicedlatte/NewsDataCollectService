package com.melllon.newsdatacollectservice.repository;

import com.melllon.newsdatacollectservice.entity.NewsSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsSiteRepository extends JpaRepository<NewsSite, Long> {
    
    List<NewsSite> findByIsActiveTrue();
    
    boolean existsByName(String name);
} 