package com.usmanaslam.sitecrawler.dto;

import java.time.LocalDateTime;

public record CrawlStatusResponse(
        Long id,
        String seedUrl,
        String state,
        int pagesDiscovered,
        int pagesCrawled,
        int pagesFailed,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        long elapsedMs
) {}
