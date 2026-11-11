package kr.ac.ync.web.repository;

import java.util.List;
import kr.ac.ync.web.domain.TrackedKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackedKeywordRepository extends JpaRepository<TrackedKeyword, Long> {

    List<TrackedKeyword> findAllByOrderByIdAsc();

    boolean existsByWord(String word);
}
