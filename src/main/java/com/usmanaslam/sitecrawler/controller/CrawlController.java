package com.usmanaslam.sitecrawler.controller;

import com.usmanaslam.sitecrawler.dto.CrawlRequest;
import com.usmanaslam.sitecrawler.dto.CrawlStatusResponse;
import com.usmanaslam.sitecrawler.dto.PageInfo;
import com.usmanaslam.sitecrawler.dto.SitemapEntry;
import com.usmanaslam.sitecrawler.service.CrawlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crawls")
@Tag(name = "Site Crawler", description = "Endpoints for managing crawls")
public class CrawlController {
    private final CrawlService crawlService;

    public CrawlController(CrawlService crawlService) {
        this.crawlService = crawlService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Start a new crawl job")
    public CrawlStatusResponse startCrawl(@RequestBody CrawlRequest request) {
        return crawlService.startCrawl(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get crawl job status")
    public CrawlStatusResponse getStatus(@PathVariable Long id) {
        return crawlService.getStatus(id);
    }

    @GetMapping
    @Operation(summary = "List all crawl jobs")
    public List<CrawlStatusResponse> getAllCrawls() {
        return crawlService.getAllCrawls();
    }

    @GetMapping("/{id}/pages")
    @Operation(summary = "Get crawled pages for a job")
    public List<PageInfo> getPages(@PathVariable Long id) {
        return crawlService.getPages(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancel a running crawl job")
    public void cancelCrawl(@PathVariable Long id) {
        crawlService.cancelCrawl(id);
    }

    @GetMapping("/{id}/sitemap")
    @Operation(summary = "Get sitemap for a crawled job")
    public List<SitemapEntry> getSitemap(@PathVariable Long id) {
        return crawlService.getSitemap(id);
    }
}
