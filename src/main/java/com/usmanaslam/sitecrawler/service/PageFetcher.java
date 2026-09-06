package com.usmanaslam.sitecrawler.service;

import com.usmanaslam.sitecrawler.config.CrawlerConfig;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class PageFetcher {
    private final HttpClient httpClient;
    private final CrawlerConfig config;

    public PageFetcher(CrawlerConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public PageFetchResult fetch(String url) {
        long startTime = System.currentTimeMillis();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .header("User-Agent", config.getUserAgent())
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long responseTimeMs = System.currentTimeMillis() - startTime;
            
            String contentType = response.headers().firstValue("Content-Type").orElse("");
            return new PageFetchResult(response.statusCode(), response.body(), contentType, responseTimeMs);
        } catch (Exception e) {
            return new PageFetchResult(500, null, null, System.currentTimeMillis() - startTime);
        }
    }

    public record PageFetchResult(int statusCode, String body, String contentType, long responseTimeMs) {}
}
