package com.usmanaslam.sitecrawler.service;

import com.usmanaslam.sitecrawler.config.CrawlerConfig;
import com.usmanaslam.sitecrawler.dto.CrawlRequest;
import com.usmanaslam.sitecrawler.dto.CrawlStatusResponse;
import com.usmanaslam.sitecrawler.dto.PageInfo;
import com.usmanaslam.sitecrawler.dto.SitemapEntry;
import com.usmanaslam.sitecrawler.exception.CrawlNotFoundException;
import com.usmanaslam.sitecrawler.model.CrawlJob;
import com.usmanaslam.sitecrawler.model.CrawlState;
import com.usmanaslam.sitecrawler.repository.CrawlJobRepository;
import com.usmanaslam.sitecrawler.repository.CrawledPageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrawlService {
    private final CrawlJobRepository crawlJobRepository;
    private final CrawledPageRepository crawledPageRepository;
    private final CrawlOrchestrator orchestrator;
    private final CrawlerConfig config;

    public CrawlService(CrawlJobRepository crawlJobRepository,
                        CrawledPageRepository crawledPageRepository,
                        CrawlOrchestrator orchestrator,
                        CrawlerConfig config) {
        this.crawlJobRepository = crawlJobRepository;
        this.crawledPageRepository = crawledPageRepository;
        this.orchestrator = orchestrator;
        this.config = config;
    }

    @Transactional
    public CrawlStatusResponse startCrawl(CrawlRequest request) {
        CrawlJob job = new CrawlJob();
        job.setSeedUrl(request.seedUrl());
        job.setMaxDepth(request.maxDepth() != null ? request.maxDepth() : config.getDefaultMaxDepth());
        job.setMaxPages(request.maxPages() != null ? request.maxPages() : config.getDefaultMaxPages());
        job.setThreads(request.threads() != null ? request.threads() : config.getDefaultThreads());
        job.setDelayMs(request.delayMs() != null ? request.delayMs() : config.getDefaultDelayMs());
        job.setState(CrawlState.QUEUED);
        
        job = crawlJobRepository.save(job);
        
        orchestrator.startCrawl(job);
        
        return mapToResponse(job);
    }

    public CrawlStatusResponse getStatus(Long id) {
        CrawlJob job = crawlJobRepository.findById(id)
                .orElseThrow(() -> new CrawlNotFoundException("Crawl not found"));
        return mapToResponse(job);
    }

    public List<CrawlStatusResponse> getAllCrawls() {
        return crawlJobRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PageInfo> getPages(Long id) {
        if (!crawlJobRepository.existsById(id)) {
            throw new CrawlNotFoundException("Crawl not found");
        }
        return crawledPageRepository.findByCrawlJobId(id).stream()
                .map(p -> new PageInfo(p.getUrl(), p.getTitle(), p.getStatusCode(), p.getLinkCount(), p.getResponseTimeMs(), p.getDepth()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelCrawl(Long id) {
        CrawlJob job = crawlJobRepository.findById(id)
                .orElseThrow(() -> new CrawlNotFoundException("Crawl not found"));
        if (job.getState() == CrawlState.RUNNING || job.getState() == CrawlState.QUEUED) {
            job.setState(CrawlState.CANCELLED);
            crawlJobRepository.save(job);
        }
    }

    public List<SitemapEntry> getSitemap(Long id) {
        if (!crawlJobRepository.existsById(id)) {
            throw new CrawlNotFoundException("Crawl not found");
        }
        return crawledPageRepository.findByCrawlJobId(id).stream()
                .map(p -> {
                    List<String> links = p.getOutgoingLinksCsv() != null && !p.getOutgoingLinksCsv().isEmpty() ? 
                            Arrays.asList(p.getOutgoingLinksCsv().split(",")) : Collections.emptyList();
                    return new SitemapEntry(p.getUrl(), links, p.getDepth());
                })
                .collect(Collectors.toList());
    }

    private CrawlStatusResponse mapToResponse(CrawlJob job) {
        long elapsed = 0;
        if (job.getStartedAt() != null) {
            LocalDateTime end = job.getCompletedAt() != null ? job.getCompletedAt() : LocalDateTime.now();
            elapsed = Duration.between(job.getStartedAt(), end).toMillis();
        }
        return new CrawlStatusResponse(job.getId(), job.getSeedUrl(), job.getState().name(),
                job.getPagesDiscovered(), job.getPagesCrawled(), job.getPagesFailed(),
                job.getStartedAt(), job.getCompletedAt(), elapsed);
    }
}
