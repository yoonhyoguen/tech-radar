package kr.ac.ync.web.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "crawl_run")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlRun {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private Source source;

    private OffsetDateTime startedAt;
    private OffsetDateTime finishedAt;
    private String status;
    private Integer fetchedCount;
    private Integer insertedCount;
    private Integer updatedCount;
    private String errorMessage;
}
