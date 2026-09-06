package com.usmanaslam.sitecrawler.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RateLimiterService {
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public void setRate(String domain, long delayMs) {
        buckets.computeIfAbsent(domain, k -> new TokenBucket(1, delayMs));
    }

    public void acquire(String domain) {
        TokenBucket bucket = buckets.get(domain);
        if (bucket == null) return;

        while (!bucket.tryAcquire()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static class TokenBucket {
        private final long capacity;
        private final long refillDelayMs;
        private final AtomicLong tokens;
        private volatile long lastRefillTime;

        public TokenBucket(long capacity, long refillDelayMs) {
            this.capacity = capacity;
            this.refillDelayMs = refillDelayMs;
            this.tokens = new AtomicLong(capacity);
            this.lastRefillTime = System.currentTimeMillis();
        }

        public synchronized boolean tryAcquire() {
            refill();
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;
            
            if (elapsed >= refillDelayMs) {
                long tokensToAdd = elapsed / refillDelayMs;
                long newTokens = Math.min(capacity, tokens.get() + tokensToAdd);
                tokens.set(newTokens);
                lastRefillTime = now;
            }
        }
    }
}
