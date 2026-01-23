package com.hong.thebaker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/privacy")
    public String privacyPage() {
        return "privacy"; // Looks for privacy.html
    }
}
