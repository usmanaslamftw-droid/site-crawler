# Site Crawler

![Java 21](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot 3.5.16](https://img.shields.io/badge/Spring%20Boot-3.5.16-brightgreen.svg)

A configurable, multi-threaded web crawler that respects robots.txt and rate limits.

## Features
- Multi-threaded crawling using CompletableFuture
- Respects `robots.txt`
- Configurable rate limiting per domain
- H2 database for storing crawl jobs and results
- Swagger UI for API documentation

## Setup
```bash
# Build
./gradlew clean build

# Run
./gradlew bootRun
```

## API Documentation
Once running, visit: http://localhost:8083/swagger-ui.html
