package com.usmanaslam.sitecrawler.exception;

public class CrawlNotFoundException extends RuntimeException {
    public CrawlNotFoundException(String message) {
        super(message);
    }
}
