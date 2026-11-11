package kr.ac.ync.web.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import kr.ac.ync.web.domain.Source;
import kr.ac.ync.web.repository.ArticleRepository;
import kr.ac.ync.web.repository.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final ArticleRepository articleRepository;
    private final SourceRepository sourceRepository;

    public Summary summary() {
        List<Source> sources = sourceRepository.findAll();
        List<Source> enabled = sources.stream().filter(Source::isEnabled).toList();

        OffsetDateTime lastCrawledAt = sources.stream()
                .map(Source::getLastCrawledAt)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        // 가장 짧은 주기의 2배가 지나도록 수집이 없으면 수집기 정지로 본다
        int shortestInterval = enabled.stream().mapToInt(Source::getIntervalMin).min().orElse(60);
        boolean stale = lastCrawledAt == null
                || lastCrawledAt.isBefore(OffsetDateTime.now().minusMinutes(shortestInterval * 2L));

        OffsetDateTime todayStart = LocalDate.now(SEOUL).atStartOfDay(SEOUL).toOffsetDateTime();

        return new Summary(
                articleRepository.count(),
                articleRepository.countByFirstSeenAtGreaterThanEqual(todayStart),
                enabled.size(),
                lastCrawledAt,
                stale);
    }
}
