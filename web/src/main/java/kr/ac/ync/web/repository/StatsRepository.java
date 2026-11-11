package kr.ac.ync.web.repository;

import java.util.List;
import kr.ac.ync.web.domain.Article;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface StatsRepository extends Repository<Article, Long> {

    @Query(value = """
            SELECT to_char(h, 'MM-DD HH24:00') AS label, count(a.id) AS cnt
            FROM generate_series(
                     date_trunc('hour', now() AT TIME ZONE 'Asia/Seoul') - interval '23 hours',
                     date_trunc('hour', now() AT TIME ZONE 'Asia/Seoul'),
                     interval '1 hour') AS h
            LEFT JOIN article a
                   ON date_trunc('hour', a.first_seen_at AT TIME ZONE 'Asia/Seoul') = h
            GROUP BY h
            ORDER BY h
            """, nativeQuery = true)
    List<LabelCount> hourlyNew();

    @Query(value = """
            SELECT k.word AS label, count(a.id) AS cnt
            FROM tracked_keyword k
            LEFT JOIN article a
                   ON a.title ILIKE '%' || k.word || '%'
                  AND a.first_seen_at >= now() - interval '7 days'
            GROUP BY k.word
            ORDER BY cnt DESC, k.word
            LIMIT 10
            """, nativeQuery = true)
    List<LabelCount> keywordTop();
}
