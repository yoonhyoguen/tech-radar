package kr.ac.ync.web.repository;

import java.time.OffsetDateTime;
import kr.ac.ync.web.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query(value = """
            SELECT a FROM Article a JOIN FETCH a.source s
            WHERE (:sourceId IS NULL OR s.id = :sourceId)
              AND LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%'))
            ORDER BY COALESCE(a.publishedAt, a.firstSeenAt) DESC, a.id DESC
            """,
            countQuery = """
            SELECT COUNT(a) FROM Article a
            WHERE (:sourceId IS NULL OR a.source.id = :sourceId)
              AND LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    Page<Article> search(@Param("q") String q, @Param("sourceId") Long sourceId, Pageable pageable);

    long countByFirstSeenAtGreaterThanEqual(OffsetDateTime from);
}
