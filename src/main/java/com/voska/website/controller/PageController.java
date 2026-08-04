package com.voska.website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/projects")
    public String projectsPage() {
        return "forward:/projects.html";
    }

    @GetMapping("/projects/{slug}")
    public String projectDetailPage(@PathVariable String slug) {
        return "forward:/project.html";
    }
}
