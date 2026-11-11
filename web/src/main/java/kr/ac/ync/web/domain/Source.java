package kr.ac.ync.web.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "source")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Source {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private String url;
    private String listSelector;
    private boolean enabled;
    private Integer intervalMin;
    private OffsetDateTime lastCrawledAt;

    public void changeSetting(boolean enabled, int intervalMin) {
        if (intervalMin < 10) {
            throw new IllegalArgumentException("수집 주기는 10분 이상이어야 합니다.");
        }
        this.enabled = enabled;
        this.intervalMin = intervalMin;
    }
}