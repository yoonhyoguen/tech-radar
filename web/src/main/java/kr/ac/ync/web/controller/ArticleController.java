package kr.ac.ync.web.controller;

import kr.ac.ync.web.domain.Article;
import kr.ac.ync.web.repository.ArticleRepository;
import kr.ac.ync.web.repository.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private static final int PAGE_SIZE = 20;

    private final ArticleRepository articleRepository;
    private final SourceRepository sourceRepository;

    @GetMapping("/articles")
    public String list(@RequestParam(defaultValue = "") String q,
                       @RequestParam(required = false) Long sourceId,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        String keyword = q.trim();
        Page<Article> articles = articleRepository.search(
                keyword, sourceId, PageRequest.of(Math.max(page, 0), PAGE_SIZE));

        model.addAttribute("articles", articles);
        model.addAttribute("sources", sourceRepository.findAllByOrderByIdAsc());
        model.addAttribute("q", keyword);
        model.addAttribute("sourceId", sourceId);
        return "articles";
    }
}
