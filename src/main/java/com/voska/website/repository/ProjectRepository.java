package com.voska.website.repository;


import com.voska.website.entity.Project;
import com.voska.website.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByTitle(String title);

    Optional<Project> findByGithubUrl(String githubUrl);

    Optional<Project> findByLiveUrl(String liveUrl);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    Optional<Project> findBySlug(String slug);

    Optional<Project> findBySlugAndStatus(String slug, ProjectStatus status);

    List<Project> findAllByOrderByCreatedAtDesc();

    List<Project> findAllByStatusOrderByCreatedAtDesc(ProjectStatus status);

    List<Project> findAllByStatusAndFeaturedTrueOrderByCreatedAtDesc(ProjectStatus status);

}
