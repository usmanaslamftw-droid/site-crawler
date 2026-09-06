package com.usmanaslam.sitecrawler.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RobotsServiceTest {

    @Test
    void testIsAllowed() {
        RobotsService service = new RobotsService();
        // Since we cannot rely on external network in tests reliably, we just test the default behavior for a domain that doesn't exist or is allowed
        assertTrue(service.isAllowed("http://example.com/test"));
    }
}
