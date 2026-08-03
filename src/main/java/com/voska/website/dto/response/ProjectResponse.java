package com.voska.website.dto.response;

import com.voska.website.entity.ProjectStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectResponse(
        Long id,
        String title,
        String slug,
        String summary,
        String description,
        String githubUrl,
        String liveUrl,
        boolean featured,
        ProjectStatus status,
        List<ProjectImageResponse> images,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
