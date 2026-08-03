package com.voska.website.controller;

import com.voska.website.dto.request.ProjectCreateRequest;
import com.voska.website.dto.request.ProjectImageCreateRequest;
import com.voska.website.dto.request.ProjectImageUpdateRequest;
import com.voska.website.dto.request.ProjectUpdateRequest;
import com.voska.website.dto.response.ProjectImageResponse;
import com.voska.website.dto.response.ProjectResponse;
import com.voska.website.service.ProjectImageService;
import com.voska.website.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class AdminProjectController {

    private final ProjectService projectService;
    private final ProjectImageService projectImageService;

    @GetMapping
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllForAdmin();
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(@PathVariable Long id) {
        return projectService.getByIdForAdmin(id);
    }

    @GetMapping("/slug/{slug}")
    public ProjectResponse getProjectBySlug(@PathVariable String slug) {
        return projectService.getBySlugForAdmin(slug);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@RequestBody ProjectCreateRequest request) {
        return projectService.create(request);
    }

    @PutMapping("/{id}")
    public ProjectResponse updateProject(
            @PathVariable Long id,
            @RequestBody ProjectUpdateRequest request
    ) {
        return projectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        projectService.delete(id);
    }

    @GetMapping("/{projectId}/images")
    public List<ProjectImageResponse> getProjectImages(@PathVariable Long projectId) {
        return projectImageService.getImagesByProject(projectId);
    }

    @PostMapping(value = "/{projectId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectImageResponse addProjectImage(
            @PathVariable Long projectId,
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") ProjectImageCreateRequest request
    ) {
        return projectImageService.addImage(projectId, file, request);
    }

    @PutMapping("/{projectId}/images/{imageId}")
    public ProjectImageResponse updateProjectImage(
            @PathVariable Long projectId,
            @PathVariable Long imageId,
            @RequestBody ProjectImageUpdateRequest request
    ) {
        return projectImageService.updateImage(projectId, imageId, request);
    }

    @PatchMapping("/{projectId}/images/{imageId}/cover")
    public ProjectImageResponse setCoverImage(
            @PathVariable Long projectId,
            @PathVariable Long imageId
    ) {
        return projectImageService.setCoverImage(projectId, imageId);
    }

    @DeleteMapping("/{projectId}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProjectImage(
            @PathVariable Long projectId,
            @PathVariable Long imageId
    ) {
        projectImageService.deleteImage(projectId, imageId);
    }
}
