package com.usmanaslam.sitecrawler.dto;

public record CrawlRequest(
        String seedUrl,
        Integer maxDepth,
        Integer maxPages,
        Integer threads,
        Long delayMs
) {}
