package com.usmanaslam.sitecrawler.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinkExtractorTest {

    @Test
    void testExtractLinks() {
        LinkExtractor extractor = new LinkExtractor();
        String html = "<html><body>" +
                "<a href=\"/about\">About</a>" +
                "<a href=\"http://external.com\">External</a>" +
                "<a href=\"javascript:void(0)\">JS</a>" +
                "</body></html>";
                
        List<String> links = extractor.extractLinks(html, "http://example.com");
        
        assertEquals(2, links.size());
        assertTrue(links.contains("http://example.com/about"));
        assertTrue(links.contains("http://external.com"));
        assertFalse(links.contains("javascript:void(0)"));
    }
}
