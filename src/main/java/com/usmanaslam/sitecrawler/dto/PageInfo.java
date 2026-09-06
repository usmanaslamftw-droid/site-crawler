package com.usmanaslam.sitecrawler.dto;

public record PageInfo(
        String url,
        String title,
        int statusCode,
        int linkCount,
        long responseTimeMs,
        int depth
) {}
