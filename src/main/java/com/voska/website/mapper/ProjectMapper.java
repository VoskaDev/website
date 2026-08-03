package com.voska.website.mapper;

import com.voska.website.dto.request.ProjectCreateRequest;
import com.voska.website.dto.request.ProjectUpdateRequest;
import com.voska.website.dto.response.ProjectImageResponse;
import com.voska.website.dto.response.ProjectResponse;
import com.voska.website.dto.response.ProjectSummaryResponse;
import com.voska.website.entity.Project;
import com.voska.website.entity.ProjectImage;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public Project toEntity(ProjectCreateRequest request, String slug) {
        Project project = new Project();
        project.setTitle(request.title());
        project.setSlug(slug);
        project.setSummary(request.summary());
        project.setDescription(request.description());
        project.setGithubUrl(request.githubUrl());
        project.setLiveUrl(request.liveUrl());
        project.setFeatured(request.featured());
        project.setStatus(request.status());
        return project;
    }

    public void updateEntity(Project project, ProjectUpdateRequest request, String slug) {
        project.setTitle(request.title());
        project.setSlug(slug);
        project.setSummary(request.summary());
        project.setDescription(request.description());
        project.setGithubUrl(request.githubUrl());
        project.setLiveUrl(request.liveUrl());
        project.setFeatured(request.featured());
        project.setStatus(request.status());
    }

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(project.getId(), project.getTitle(), project.getSlug(),
                project.getSummary(), project.getDescription(), project.getGithubUrl(),
                project.getLiveUrl(), project.isFeatured(), project.getStatus(),
                project.getImages() == null ? java.util.List.of() : project.getImages().stream().map(this::toImageResponse).toList(),
                project.getCreatedAt(), project.getUpdatedAt());
    }

    public ProjectSummaryResponse toSummaryResponse(Project project) {
        String coverUrl = (project.getImages() == null ? java.util.stream.Stream.<ProjectImage>empty() : project.getImages().stream())
                .filter(ProjectImage::isCover)
                .findFirst()
                .map(ProjectImage::getImageUrl)
                .orElse(null);
        return new ProjectSummaryResponse(project.getId(), project.getTitle(), project.getSlug(),
                project.getSummary(), coverUrl, project.isFeatured(), project.getCreatedAt());
    }

    public ProjectImageResponse toImageResponse(ProjectImage image) {
        return new ProjectImageResponse(image.getId(), image.getImageUrl(), image.getAltText(),
                image.getDisplayOrder(), image.isCover(), image.getCreatedAt());
    }
}
