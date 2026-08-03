package com.voska.website.controller;

import com.voska.website.dto.response.ProjectResponse;
import com.voska.website.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class PublicProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<ProjectResponse> getPublishedProjects() {
        return projectService.getPublishedProjects();
    }

    @GetMapping("/featured")
    public List<ProjectResponse> getFeaturedProjects() {
        return projectService.getFeaturedProjects();
    }

    @GetMapping("/{slug}")
    public ProjectResponse getProjectBySlug(@PathVariable String slug) {
        return projectService.getProjectBySlug(slug);
    }
}
