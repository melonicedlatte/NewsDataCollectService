package com.melllon.newsdatacollectservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NewsDataCollectServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsDataCollectServiceApplication.class, args);
    }

}
