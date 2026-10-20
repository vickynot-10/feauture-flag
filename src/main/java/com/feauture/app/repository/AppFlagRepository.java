package com.feauture.app.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.feauture.app.entity.AppFlags;

public interface AppFlagRepository extends JpaRepository<AppFlags, Long> {

    boolean existsByApp_IdAndFlagKey(Long appId, String flagKey);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE AppFlags f SET f.currentVersion = :newVersion, f.updatedAt = :now
            WHERE f.id = :id AND f.currentVersion = :expectedVersion
            """)
    int insertVersion(@Param("id") Long id,
            @Param("expectedVersion") int expectedVersion,
            @Param("newVersion") int newVersion,
            @Param("now") Instant now);

}
