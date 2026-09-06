package com.usmanaslam.sitecrawler.service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LinkExtractor {
    private static final Pattern LINK_PATTERN = Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1");

    public List<String> extractLinks(String html, String baseUrl) {
        List<String> links = new ArrayList<>();
        if (html == null || html.isEmpty()) return links;

        try {
            URI baseUri = new URI(baseUrl);
            Matcher matcher = LINK_PATTERN.matcher(html);
            
            while (matcher.find()) {
                String href = matcher.group(2).trim();
                
                // Filter out non-http links, anchors, JS, mailto
                if (href.startsWith("javascript:") || href.startsWith("mailto:") || href.startsWith("#")) {
                    continue;
                }

                try {
                    URI resolvedUri = baseUri.resolve(href);
                    String normalized = resolvedUri.getScheme() + "://" + resolvedUri.getHost();
                    if (resolvedUri.getPort() != -1) {
                        normalized += ":" + resolvedUri.getPort();
                    }
                    if (resolvedUri.getPath() != null) {
                        normalized += resolvedUri.getPath();
                    }
                    if (resolvedUri.getQuery() != null) {
                        normalized += "?" + resolvedUri.getQuery();
                    }
                    
                    if (normalized.startsWith("http")) {
                        links.add(normalized);
                    }
                } catch (IllegalArgumentException e) {
                    // Invalid link format, ignore
                }
            }
        } catch (Exception e) {
            // Ignore parse errors
        }
        
        return links;
    }
}
