package com.voska.website.service;

import com.voska.website.dto.request.ProjectImageCreateRequest;
import com.voska.website.dto.request.ProjectImageUpdateRequest;
import com.voska.website.dto.response.ProjectImageResponse;
import com.voska.website.entity.Project;
import com.voska.website.entity.ProjectImage;
import com.voska.website.exception.ImageNotFoundException;
import com.voska.website.exception.ProjectNotFoundException;
import com.voska.website.mapper.ProjectImageMapper;
import com.voska.website.repository.ProjectImageRepository;
import com.voska.website.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectImageService {

    private final ProjectImageRepository projectImageRepository;
    private final ProjectRepository projectRepository;
    private final ProjectImageMapper mapper;
    private final StorageService storageService;

    @Transactional
    public ProjectImageResponse addImage(
            Long projectId,
            MultipartFile file,
            ProjectImageCreateRequest request
    ) {
        Project project = findProjectById(projectId);

        String imageUrl = storageService.storeProjectImage(projectId, file);

        if (request.cover()) {
            removeCurrentCover(projectId);
        }

        ProjectImage image = mapper.toEntity(request, project, imageUrl);

        if (image.getAltText() == null || image.getAltText().isBlank()) {
            image.setAltText(project.getTitle());
        }

        image.setCreatedAt(LocalDateTime.now());

        ProjectImage savedImage = projectImageRepository.save(image);

        return mapper.toResponse(savedImage);
    }

    public List<ProjectImageResponse> getImagesByProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }

        return projectImageRepository
                .findAllByProjectIdOrderByDisplayOrderAsc(projectId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public ProjectImageResponse updateImage(
            Long projectId,
            Long imageId,
            ProjectImageUpdateRequest request
    ) {
        ProjectImage image = findImage(projectId, imageId);

        if (request.cover() && !image.isCover()) {
            removeCurrentCover(projectId);
        }

        mapper.updateEntity(image, request);

        return mapper.toResponse(image);
    }

    @Transactional
    public ProjectImageResponse setCoverImage(
            Long projectId,
            Long imageId
    ) {
        ProjectImage image = findImage(projectId, imageId);

        removeCurrentCover(projectId);

        image.setCover(true);

        return mapper.toResponse(image);
    }

    @Transactional
    public void deleteImage(
            Long projectId,
            Long imageId
    ) {
        ProjectImage image = findImage(projectId, imageId);

        storageService.delete(image.getImageUrl());
        projectImageRepository.delete(image);
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    private ProjectImage findImage(Long projectId, Long imageId) {
        return projectImageRepository
                .findByIdAndProjectId(imageId, projectId)
                .orElseThrow(() -> new ImageNotFoundException(imageId));
    }

    private void removeCurrentCover(Long projectId) {
        projectImageRepository
                .findByProjectIdAndCoverTrue(projectId)
                .ifPresent(image -> image.setCover(false));
    }
}