package com.voska.website.dto.request;

public record SiteProfileUpdateRequest(
        String githubUrl,
        String linkedinUrl,
        String discordUrl,
        String websiteUrl,
        String xUrl,
        String instagramUrl,
        String resumeUrl,
        String contactEmail
) {}
