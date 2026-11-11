package kr.ac.ync.web.controller;

import kr.ac.ync.web.domain.Source;
import kr.ac.ync.web.repository.SourceRepository;
import kr.ac.ync.web.service.SourceService;
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
public class SourceController {

    private final SourceRepository sourceRepository;
    private final SourceService sourceService;

    @GetMapping("/sources")
    public String list(Model model) {
        model.addAttribute("sources", sourceRepository.findAllByOrderByIdAsc());
        return "sources";
    }

    @PostMapping("/sources/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam(defaultValue = "false") boolean enabled,
                         @RequestParam int intervalMin,
                         RedirectAttributes redirect) {
        try {
            Source source = sourceService.changeSetting(id, enabled, intervalMin);
            redirect.addFlashAttribute("message",
                    source.getName() + " 설정을 저장했습니다. 수집기의 다음 확인(1분 이내)부터 반영됩니다.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sources";
    }
}
