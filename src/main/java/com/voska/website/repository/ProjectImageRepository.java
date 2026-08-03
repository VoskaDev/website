package com.voska.website.repository;

import com.voska.website.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {

    public Optional<ProjectImage> findByImageUrl(String imageUrl);

    public List<ProjectImage> findByProjectId(Long projectId);

    List<ProjectImage> findAllByProjectIdOrderByDisplayOrderAsc(Long projectId);

    Optional<ProjectImage> findByIdAndProjectId(Long imageId, Long projectId);

    boolean existsByProjectIdAndCoverTrue(Long projectId);

    Optional<ProjectImage> findByProjectIdAndCoverTrue(Long projectId);
}
