package kr.ac.ync.web.service;

import kr.ac.ync.web.domain.TrackedKeyword;
import kr.ac.ync.web.repository.TrackedKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final TrackedKeywordRepository keywordRepository;

    @Transactional
    public void add(String word) {
        String trimmed = word == null ? "" : word.trim();
        if (trimmed.isEmpty() || trimmed.length() > 100) {
            throw new IllegalArgumentException("키워드는 1~100자로 입력해 주세요.");
        }
        if (keywordRepository.existsByWord(trimmed)) {
            throw new IllegalArgumentException("이미 등록된 키워드입니다.");
        }
        keywordRepository.save(new TrackedKeyword(trimmed));
    }

    @Transactional
    public void delete(Long id) {
        keywordRepository.deleteById(id);
    }
}
