package com.usmanaslam.sitecrawler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crawled_pages")
public class CrawledPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crawl_job_id")
    private CrawlJob crawlJob;

    @Column(length = 2048)
    private String url;
    
    private String title;
    private int statusCode;
    private String contentType;
    private int linkCount;
    private long responseTimeMs;
    private int depth;
    private LocalDateTime crawledAt;
    
    @Column(length = 2048)
    private String errorMessage;
    
    // For storing extracted outgoing links simply (as string, separated by something, or as a collection)
    // To keep it simple, we use a Lob or just ignore storing the exact mapping in DB for this assignment
    @Lob
    private String outgoingLinksCsv; // Simple way to store links for sitemap

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CrawlJob getCrawlJob() { return crawlJob; }
    public void setCrawlJob(CrawlJob crawlJob) { this.crawlJob = crawlJob; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public int getLinkCount() { return linkCount; }
    public void setLinkCount(int linkCount) { this.linkCount = linkCount; }
    public long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }
    public LocalDateTime getCrawledAt() { return crawledAt; }
    public void setCrawledAt(LocalDateTime crawledAt) { this.crawledAt = crawledAt; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getOutgoingLinksCsv() { return outgoingLinksCsv; }
    public void setOutgoingLinksCsv(String outgoingLinksCsv) { this.outgoingLinksCsv = outgoingLinksCsv; }
}
