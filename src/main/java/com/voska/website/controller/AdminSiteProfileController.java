package com.voska.website.controller;

import com.voska.website.dto.request.SiteProfileUpdateRequest;
import com.voska.website.dto.response.SiteProfileResponse;
import com.voska.website.service.SiteProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
public class AdminSiteProfileController {

    private final SiteProfileService siteProfileService;

    @PutMapping
    public SiteProfileResponse updateProfile(@RequestBody SiteProfileUpdateRequest request) {
        return siteProfileService.updateProfile(request);
    }
}
