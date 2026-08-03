package com.voska.website.service;

import com.voska.website.dto.request.ProjectCreateRequest;
import com.voska.website.dto.request.ProjectUpdateRequest;
import com.voska.website.dto.response.ProjectResponse;
import com.voska.website.entity.Project;
import com.voska.website.entity.ProjectStatus;
import com.voska.website.exception.DuplicateSlugException;
import com.voska.website.exception.ProjectNotFoundException;
import com.voska.website.mapper.ProjectMapper;
import com.voska.website.repository.ProjectRepository;
import com.voska.website.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final SlugUtil slugUtil;

    // ADMIN CRUD

    @Transactional
    public ProjectResponse create(ProjectCreateRequest request) {
        String slug = slugUtil.generate(request.title());
        checkSlugForCreate(slug);

        Project project = mapper.toEntity(request, slug);
        LocalDateTime now = LocalDateTime.now();
        project.setCreatedAt(now);
        project.setUpdatedAt(now);

        return mapper.toResponse(projectRepository.save(project));
    }

    public List<ProjectResponse> getAllForAdmin() {
        return projectRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ProjectResponse getByIdForAdmin(Long id) {
        return mapper.toResponse(findById(id));
    }

    public ProjectResponse getBySlugForAdmin(String slug) {
        Project project = projectRepository.findBySlug(slug)
                .orElseThrow(() -> new ProjectNotFoundException(slug));
        return mapper.toResponse(project);
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectUpdateRequest request) {
        Project project = findById(id);
        String slug = slugUtil.generate(request.title());

        if (projectRepository.existsBySlugAndIdNot(slug, id)) {
            throw new DuplicateSlugException(slug);
        }

        mapper.updateEntity(project, request, slug);
        project.setUpdatedAt(LocalDateTime.now());
        return mapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    public void delete(Long id) {
        Project project = findById(id);
        projectRepository.delete(project);
    }

    public List<ProjectResponse> getPublishedProjects() {
        return projectRepository.findAllByStatusOrderByCreatedAtDesc(ProjectStatus.PUBLISHED)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<ProjectResponse> getFeaturedProjects() {
        return projectRepository
                .findAllByStatusAndFeaturedTrueOrderByCreatedAtDesc(ProjectStatus.PUBLISHED)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ProjectResponse getProjectBySlug(String slug) {
        Project project = projectRepository.findBySlugAndStatus(slug, ProjectStatus.PUBLISHED)
                .orElseThrow(() -> new ProjectNotFoundException(slug));
        return mapper.toResponse(project);
    }

    private Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    private void checkSlugForCreate(String slug) {
        if (projectRepository.existsBySlug(slug)) {
            throw new DuplicateSlugException(slug);
        }
    }
}
