package kr.ac.ync.web.controller;

import kr.ac.ync.web.repository.CrawlRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RunController {

    private static final int PAGE_SIZE = 30;

    private final CrawlRunRepository crawlRunRepository;

    @GetMapping("/runs")
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        long total = crawlRunRepository.count();
        long success = crawlRunRepository.countByStatus("SUCCESS");

        model.addAttribute("runs", crawlRunRepository.findAllByOrderByIdDesc(PageRequest.of(Math.max(page, 0), PAGE_SIZE)));
        model.addAttribute("total", total);
        model.addAttribute("failed", crawlRunRepository.countByStatus("FAILED"));
        model.addAttribute("successRate", total == 0 ? 0 : Math.round(success * 1000.0 / total) / 10.0);
        return "runs";
    }
}
