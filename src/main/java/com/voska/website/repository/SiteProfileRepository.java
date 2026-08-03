package com.voska.website.repository;

import com.voska.website.entity.SiteProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteProfileRepository extends JpaRepository<SiteProfile, Long> {
    Optional<SiteProfile> findFirstByOrderByIdAsc();
}
