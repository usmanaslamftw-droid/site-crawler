package com.usmanaslam.sitecrawler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "crawler")
public class CrawlerConfig {
    private int defaultMaxDepth = 3;
    private int defaultMaxPages = 100;
    private int defaultThreads = 5;
    private long defaultDelayMs = 1000;
    private String userAgent = "SiteCrawler/1.0";

    // Getters and Setters
    public int getDefaultMaxDepth() { return defaultMaxDepth; }
    public void setDefaultMaxDepth(int defaultMaxDepth) { this.defaultMaxDepth = defaultMaxDepth; }
    public int getDefaultMaxPages() { return defaultMaxPages; }
    public void setDefaultMaxPages(int defaultMaxPages) { this.defaultMaxPages = defaultMaxPages; }
    public int getDefaultThreads() { return defaultThreads; }
    public void setDefaultThreads(int defaultThreads) { this.defaultThreads = defaultThreads; }
    public long getDefaultDelayMs() { return defaultDelayMs; }
    public void setDefaultDelayMs(long defaultDelayMs) { this.defaultDelayMs = defaultDelayMs; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}
