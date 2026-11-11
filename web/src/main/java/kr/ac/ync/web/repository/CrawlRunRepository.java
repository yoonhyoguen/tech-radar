package kr.ac.ync.web.repository;

import java.util.List;
import kr.ac.ync.web.domain.CrawlRun;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrawlRunRepository extends JpaRepository<CrawlRun, Long> {

    @EntityGraph(attributePaths = "source")
    List<CrawlRun> findTop6ByOrderByIdDesc();

    @EntityGraph(attributePaths = "source")
    Page<CrawlRun> findAllByOrderByIdDesc(Pageable pageable);

    long countByStatus(String status);
}
