package com.usmanaslam.sitecrawler.dto;

import java.util.List;

public record SitemapEntry(
        String url,
        List<String> outgoingLinks,
        int depth
) {}
