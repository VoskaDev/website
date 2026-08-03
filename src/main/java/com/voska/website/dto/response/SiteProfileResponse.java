package com.voska.website.dto.response;

import java.time.LocalDateTime;

public record SiteProfileResponse(
        String githubUrl,
        String linkedinUrl,
        String discordUrl,
        String websiteUrl,
        String xUrl,
        String instagramUrl,
        String resumeUrl,
        String contactEmail,
        LocalDateTime updatedAt
) {}
