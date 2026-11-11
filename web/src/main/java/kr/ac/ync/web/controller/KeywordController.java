package kr.ac.ync.web.controller;

import kr.ac.ync.web.repository.StatsRepository;
import kr.ac.ync.web.repository.TrackedKeywordRepository;
import kr.ac.ync.web.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;
    private final TrackedKeywordRepository keywordRepository;
    private final StatsRepository statsRepository;

    @GetMapping("/keywords")
    public String list(Model model) {
        model.addAttribute("keywords", keywordRepository.findAllByOrderByIdAsc());
        model.addAttribute("top", statsRepository.keywordTop());
        return "keywords";
    }

    @PostMapping("/keywords")
    public String add(@RequestParam String word, RedirectAttributes redirect) {
        try {
            keywordService.add(word);
            redirect.addFlashAttribute("message", "키워드를 등록했습니다.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/keywords";
    }

    @PostMapping("/keywords/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        keywordService.delete(id);
        redirect.addFlashAttribute("message", "키워드를 삭제했습니다.");
        return "redirect:/keywords";
    }
}
