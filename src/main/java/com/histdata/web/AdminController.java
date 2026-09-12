package com.histdata.web;

import com.histdata.audit.DownloadAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final DownloadAuditRepository auditRepository;

    @GetMapping("/admin/downloads")
    String downloads(Model model) {
        model.addAttribute("downloads", auditRepository.findTop100ByOrderByStartedAtDesc());
        return "admin-downloads";
    }
}

