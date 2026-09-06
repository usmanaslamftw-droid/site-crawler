package com.usmanaslam.sitecrawler.service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RobotsService {
    private final ConcurrentHashMap<String, List<String>> cache = new ConcurrentHashMap<>();
    private final HttpClient httpClient;

    public RobotsService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public boolean isAllowed(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            if (domain == null) return true;
            
            cache.computeIfAbsent(domain, this::fetchAndParse);
            
            List<String> disallowedPaths = cache.get(domain);
            String path = uri.getPath();
            if (path == null || path.isEmpty()) path = "/";
            
            for (String disallowed : disallowedPaths) {
                if (path.startsWith(disallowed)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public List<String> fetchAndParse(String domain) {
        List<String> disallowed = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://" + domain + "/robots.txt"))
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();
                    
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String[] lines = response.body().split("\\r?\\n");
                boolean userAgentMatch = false;
                for (String line : lines) {
                    line = line.trim();
                    if (line.toLowerCase().startsWith("user-agent:")) {
                        String ua = line.substring(11).trim();
                        userAgentMatch = ua.equals("*");
                    } else if (userAgentMatch && line.toLowerCase().startsWith("disallow:")) {
                        String path = line.substring(9).trim();
                        if (!path.isEmpty()) {
                            disallowed.add(path);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Log error, default allow all
        }
        return disallowed;
    }
}
