package com.voska.website.dto.response;

import java.time.LocalDateTime;

public record ProjectImageResponse(

        Long id,

        String imageUrl,

        String altText,

        int displayOrder,

        boolean cover,

        LocalDateTime createdAt
) {
}