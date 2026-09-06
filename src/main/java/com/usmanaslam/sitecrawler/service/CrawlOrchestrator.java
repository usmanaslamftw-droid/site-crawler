package com.usmanaslam.sitecrawler.service;

import com.usmanaslam.sitecrawler.model.CrawlJob;
import com.usmanaslam.sitecrawler.model.CrawlState;
import com.usmanaslam.sitecrawler.model.CrawledPage;
import com.usmanaslam.sitecrawler.repository.CrawlJobRepository;
import com.usmanaslam.sitecrawler.repository.CrawledPageRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CrawlOrchestrator {
    private final CrawlJobRepository crawlJobRepository;
    private final CrawledPageRepository crawledPageRepository;
    private final RobotsService robotsService;
    private final RateLimiterService rateLimiterService;
    private final PageFetcher pageFetcher;
    private final LinkExtractor linkExtractor;

    public CrawlOrchestrator(CrawlJobRepository crawlJobRepository,
                             CrawledPageRepository crawledPageRepository,
                             RobotsService robotsService,
                             RateLimiterService rateLimiterService,
                             PageFetcher pageFetcher,
                             LinkExtractor linkExtractor) {
        this.crawlJobRepository = crawlJobRepository;
        this.crawledPageRepository = crawledPageRepository;
        this.robotsService = robotsService;
        this.rateLimiterService = rateLimiterService;
        this.pageFetcher = pageFetcher;
        this.linkExtractor = linkExtractor;
    }

    record CrawlTask(String url, int depth) {}

    @Async("crawlerExecutor")
    public void startCrawl(CrawlJob initialJob) {
        final AtomicReference<CrawlJob> jobRef = new AtomicReference<>(initialJob);
        try {
            CrawlJob job = jobRef.get();
            job.setState(CrawlState.RUNNING);
            job.setStartedAt(LocalDateTime.now());
            jobRef.set(crawlJobRepository.save(job));

            String seedUrl = job.getSeedUrl();
            URI seedUri = new URI(seedUrl);
            String domain = seedUri.getHost();

            rateLimiterService.setRate(domain, job.getDelayMs());

            BlockingQueue<CrawlTask> queue = new LinkedBlockingQueue<>();
            ConcurrentHashMap<String, Boolean> visited = new ConcurrentHashMap<>();

            queue.add(new CrawlTask(seedUrl, 0));
            visited.put(seedUrl, true);
            jobRef.get().incrementDiscovered();

            ExecutorService executor = Executors.newFixedThreadPool(job.getThreads());
            AtomicInteger activeTasks = new AtomicInteger(0);

            while (true) {
                CrawlJob currentJob = crawlJobRepository.findById(jobRef.get().getId()).orElse(jobRef.get());
                jobRef.set(currentJob);
                if (currentJob.getState() == CrawlState.CANCELLED) {
                    break;
                }
                if (currentJob.getPagesCrawled() >= currentJob.getMaxPages()) {
                    break;
                }

                CrawlTask task = queue.poll(500, TimeUnit.MILLISECONDS);
                if (task == null) {
                    if (activeTasks.get() == 0) {
                        break;
                    }
                    continue;
                }

                final int maxDepth = currentJob.getMaxDepth();
                activeTasks.incrementAndGet();
                CompletableFuture.runAsync(() -> {
                    try {
                        if (!robotsService.isAllowed(task.url())) {
                            jobRef.get().incrementFailed();
                            return;
                        }

                        rateLimiterService.acquire(domain);

                        PageFetcher.PageFetchResult result = pageFetcher.fetch(task.url());

                        CrawledPage page = new CrawledPage();
                        page.setCrawlJob(jobRef.get());
                        page.setUrl(task.url());
                        page.setStatusCode(result.statusCode());
                        page.setContentType(result.contentType());
                        page.setResponseTimeMs(result.responseTimeMs());
                        page.setDepth(task.depth());
                        page.setCrawledAt(LocalDateTime.now());

                        if (result.statusCode() == 200 && result.body() != null) {
                            String title = extractTitle(result.body());
                            page.setTitle(title != null && title.length() > 255 ? title.substring(0, 255) : title);

                            List<String> links = linkExtractor.extractLinks(result.body(), task.url());
                            page.setLinkCount(links.size());
                            page.setOutgoingLinksCsv(String.join(",", links));

                            jobRef.get().incrementCrawled();

                            if (task.depth() < maxDepth) {
                                for (String link : links) {
                                    if (visited.putIfAbsent(link, true) == null) {
                                        queue.add(new CrawlTask(link, task.depth() + 1));
                                        jobRef.get().incrementDiscovered();
                                    }
                                }
                            }
                        } else {
                            jobRef.get().incrementFailed();
                        }

                        crawledPageRepository.save(page);
                        crawlJobRepository.save(jobRef.get());

                    } catch (Exception e) {
                        jobRef.get().incrementFailed();
                    } finally {
                        activeTasks.decrementAndGet();
                    }
                }, executor);
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);

            CrawlJob finalJob = jobRef.get();
            if (finalJob.getState() != CrawlState.CANCELLED) {
                finalJob.setState(CrawlState.COMPLETED);
            }
        } catch (Exception e) {
            jobRef.get().setState(CrawlState.FAILED);
        } finally {
            CrawlJob finalJob = jobRef.get();
            finalJob.setCompletedAt(LocalDateTime.now());
            crawlJobRepository.save(finalJob);
        }
    }

    private String extractTitle(String html) {
        int start = html.indexOf("<title>");
        int end = html.indexOf("</title>");
        if (start != -1 && end != -1 && end > start + 7) {
            return html.substring(start + 7, end).trim();
        }
        return "No Title";
    }
}
