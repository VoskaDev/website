package com.voska.website.controller;

import com.voska.website.dto.response.SiteProfileResponse;
import com.voska.website.service.SiteProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class SiteProfileController {

    private final SiteProfileService siteProfileService;

    @GetMapping
    public SiteProfileResponse getProfile() {
        return siteProfileService.getProfile();
    }
}
