package com.voska.website.service;

import com.voska.website.dto.request.SiteProfileUpdateRequest;
import com.voska.website.dto.response.SiteProfileResponse;
import com.voska.website.entity.SiteProfile;
import com.voska.website.repository.SiteProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteProfileService {

    private final SiteProfileRepository siteProfileRepository;

    public SiteProfileResponse getProfile() {
        return siteProfileRepository.findFirstByOrderByIdAsc()
                .map(this::toResponse)
                .orElseGet(this::emptyResponse);
    }

    @Transactional
    public SiteProfileResponse updateProfile(SiteProfileUpdateRequest request) {
        SiteProfile profile = siteProfileRepository.findFirstByOrderByIdAsc()
                .orElseGet(SiteProfile::new);

        profile.setGithubUrl(normalizeUrl(request.githubUrl(), "githubUrl"));
        profile.setLinkedinUrl(normalizeUrl(request.linkedinUrl(), "linkedinUrl"));
        profile.setDiscordUrl(normalizeUrl(request.discordUrl(), "discordUrl"));
        profile.setWebsiteUrl(normalizeUrl(request.websiteUrl(), "websiteUrl"));
        profile.setXUrl(normalizeUrl(request.xUrl(), "xUrl"));
        profile.setInstagramUrl(normalizeUrl(request.instagramUrl(), "instagramUrl"));
        profile.setResumeUrl(normalizeUrl(request.resumeUrl(), "resumeUrl"));
        profile.setContactEmail(normalizeEmail(request.contactEmail()));

        return toResponse(siteProfileRepository.save(profile));
    }

    private SiteProfileResponse toResponse(SiteProfile profile) {
        return new SiteProfileResponse(
                profile.getGithubUrl(),
                profile.getLinkedinUrl(),
                profile.getDiscordUrl(),
                profile.getWebsiteUrl(),
                profile.getXUrl(),
                profile.getInstagramUrl(),
                profile.getResumeUrl(),
                profile.getContactEmail(),
                profile.getUpdatedAt()
        );
    }

    private SiteProfileResponse emptyResponse() {
        return new SiteProfileResponse(null, null, null, null, null, null, null, null, null);
    }

    private String normalizeUrl(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String url = value.trim();
        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(fieldName + " must be a valid URL");
        }

        if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                || uri.getHost() == null) {
            throw new IllegalArgumentException(fieldName + " must start with http:// or https://");
        }
        return url;
    }

    private String normalizeEmail(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String email = value.trim().toLowerCase();
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("contactEmail must be a valid email address");
        }
        return email;
    }
}
