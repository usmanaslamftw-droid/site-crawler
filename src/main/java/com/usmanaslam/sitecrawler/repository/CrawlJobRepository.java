package com.usmanaslam.sitecrawler.repository;

import com.usmanaslam.sitecrawler.model.CrawlJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlJobRepository extends JpaRepository<CrawlJob, Long> {
}
