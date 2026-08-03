package com.voska.website.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ProjectImageUpdateRequest(

        @Size(max = 255)
        String altText,

        @Min(0)
        int displayOrder,

        boolean cover
) {
}