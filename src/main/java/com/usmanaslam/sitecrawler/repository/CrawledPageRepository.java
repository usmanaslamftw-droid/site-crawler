package com.usmanaslam.sitecrawler.repository;

import com.usmanaslam.sitecrawler.model.CrawledPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawledPageRepository extends JpaRepository<CrawledPage, Long> {
    List<CrawledPage> findByCrawlJobId(Long crawlJobId);
    List<CrawledPage> findByCrawlJobIdAndDepth(Long crawlJobId, int depth);
}
