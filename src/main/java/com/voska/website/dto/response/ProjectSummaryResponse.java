package com.voska.website.dto.response;

import java.time.LocalDateTime;

public record ProjectSummaryResponse(
        Long id,
        String title,
        String slug,
        String summary,
        String coverImageUrl,
        boolean featured,
        LocalDateTime createdAt
) {}
