package com.usmanaslam.sitecrawler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crawl_jobs")
public class CrawlJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seedUrl;
    private int maxDepth;
    private int maxPages;
    private int threads;
    private long delayMs;

    @Enumerated(EnumType.STRING)
    private CrawlState state;

    private int pagesDiscovered = 0;
    private int pagesCrawled = 0;
    private int pagesFailed = 0;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSeedUrl() { return seedUrl; }
    public void setSeedUrl(String seedUrl) { this.seedUrl = seedUrl; }
    public int getMaxDepth() { return maxDepth; }
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }
    public int getMaxPages() { return maxPages; }
    public void setMaxPages(int maxPages) { this.maxPages = maxPages; }
    public int getThreads() { return threads; }
    public void setThreads(int threads) { this.threads = threads; }
    public long getDelayMs() { return delayMs; }
    public void setDelayMs(long delayMs) { this.delayMs = delayMs; }
    public CrawlState getState() { return state; }
    public void setState(CrawlState state) { this.state = state; }
    public int getPagesDiscovered() { return pagesDiscovered; }
    public void setPagesDiscovered(int pagesDiscovered) { this.pagesDiscovered = pagesDiscovered; }
    public int getPagesCrawled() { return pagesCrawled; }
    public void setPagesCrawled(int pagesCrawled) { this.pagesCrawled = pagesCrawled; }
    public int getPagesFailed() { return pagesFailed; }
    public void setPagesFailed(int pagesFailed) { this.pagesFailed = pagesFailed; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public synchronized void incrementDiscovered() { this.pagesDiscovered++; }
    public synchronized void incrementCrawled() { this.pagesCrawled++; }
    public synchronized void incrementFailed() { this.pagesFailed++; }
}
