package kr.ac.ync.web.service;

import java.time.OffsetDateTime;

public record Summary(
        long totalArticles,
        long todayNew,
        long activeSources,
        OffsetDateTime lastCrawledAt,
        boolean collectorStale) {
}
