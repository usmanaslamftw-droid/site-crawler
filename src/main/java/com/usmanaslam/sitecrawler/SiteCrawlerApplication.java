package com.usmanaslam.sitecrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SiteCrawlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SiteCrawlerApplication.class, args);
    }
}
