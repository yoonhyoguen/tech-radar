package kr.ac.ync.web.repository;

import java.util.List;
import kr.ac.ync.web.domain.Source;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SourceRepository extends JpaRepository<Source, Long> {

    List<Source> findAllByOrderByIdAsc();

    long countByEnabledTrue();
}
