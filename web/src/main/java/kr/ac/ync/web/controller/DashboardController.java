package kr.ac.ync.web.controller;

import java.util.List;
import kr.ac.ync.web.repository.CrawlRunRepository;
import kr.ac.ync.web.repository.LabelCount;
import kr.ac.ync.web.repository.StatsRepository;
import kr.ac.ync.web.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final StatsRepository statsRepository;
    private final CrawlRunRepository crawlRunRepository;

    @GetMapping("/")
    public String dashboard(Model model) {
        List<LabelCount> hourly = statsRepository.hourlyNew();
        List<LabelCount> keywords = statsRepository.keywordTop();

        model.addAttribute("summary", dashboardService.summary());
        model.addAttribute("hourlyLabels", hourly.stream().map(LabelCount::getLabel).toList());
        model.addAttribute("hourlyCounts", hourly.stream().map(LabelCount::getCnt).toList());
        model.addAttribute("keywordLabels", keywords.stream().map(LabelCount::getLabel).toList());
        model.addAttribute("keywordCounts", keywords.stream().map(LabelCount::getCnt).toList());
        model.addAttribute("recentRuns", crawlRunRepository.findTop6ByOrderByIdDesc());
        return "dashboard";
    }
}
