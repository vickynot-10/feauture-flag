package com.feauture.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.feauture.app.entity.AppFlagVersions;

public interface AppFlagVersionRepository extends JpaRepository<AppFlagVersions, Long> {
    Optional<AppFlagVersions> findByFlag_IdAndVersion(Long flagId, Integer version);
}
