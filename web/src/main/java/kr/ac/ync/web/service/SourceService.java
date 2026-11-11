package kr.ac.ync.web.service;

import kr.ac.ync.web.domain.Source;
import kr.ac.ync.web.repository.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SourceService {

    private final SourceRepository sourceRepository;

    @Transactional
    public Source changeSetting(Long id, boolean enabled, int intervalMin) {
        Source source = sourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("소스를 찾을 수 없습니다."));
        source.changeSetting(enabled, intervalMin);
        return source;
    }
}
