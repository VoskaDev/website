package com.voska.website.dto.request;

import com.voska.website.entity.ProjectStatus;

public record ProjectUpdateRequest(
        String title,
        String summary,
        String description,
        String githubUrl,
        String liveUrl,
        boolean featured,
        ProjectStatus status
) {}
