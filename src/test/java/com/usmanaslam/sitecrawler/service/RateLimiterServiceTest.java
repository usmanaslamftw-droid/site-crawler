package com.usmanaslam.sitecrawler.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterServiceTest {

    @Test
    void testRateLimiter() {
        RateLimiterService service = new RateLimiterService();
        service.setRate("example.com", 100);
        
        long start = System.currentTimeMillis();
        service.acquire("example.com"); // first is immediate
        service.acquire("example.com"); // second waits ~100ms
        long elapsed = System.currentTimeMillis() - start;
        
        assertTrue(elapsed >= 90, "Elapsed should be at least ~100ms");
    }
}
