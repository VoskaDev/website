package com.voska.website.mapper;

import com.voska.website.dto.request.ProjectImageCreateRequest;
import com.voska.website.dto.request.ProjectImageUpdateRequest;
import com.voska.website.dto.response.ProjectImageResponse;
import com.voska.website.entity.Project;
import com.voska.website.entity.ProjectImage;
import org.springframework.stereotype.Component;

@Component
public class ProjectImageMapper {

    public ProjectImage toEntity(
            ProjectImageCreateRequest request,
            Project project,
            String imageUrl
    ) {
        ProjectImage image = new ProjectImage();

        image.setProject(project);
        image.setImageUrl(imageUrl);
        image.setAltText(request.altText());
        image.setDisplayOrder(request.displayOrder());
        image.setCover(request.cover());

        return image;
    }

    public void updateEntity(
            ProjectImage image,
            ProjectImageUpdateRequest request
    ) {
        image.setAltText(request.altText());
        image.setDisplayOrder(request.displayOrder());
        image.setCover(request.cover());
    }

    public ProjectImageResponse toResponse(ProjectImage image) {
        return new ProjectImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.getAltText(),
                image.getDisplayOrder(),
                image.isCover(),
                image.getCreatedAt()
        );
    }
}