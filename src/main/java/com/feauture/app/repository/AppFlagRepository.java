package com.feauture.app.repository;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.feauture.app.entity.AppFlags;
import com.feauture.app.entity.AppUser;

public interface AppFlagRepository extends JpaRepository<AppFlags, Long>, JpaSpecificationExecutor<AppFlags> {

        interface FlagItem {
                Long getId();

                String getName();

                String getFlagKey();

                int getCurrentVersion();

                Instant getCreatedAt();

                Instant getUpdatedAt();

                boolean getEnabled();

                AppInfo getApp();

                UserInfo getCreatedBy();

                UserInfo getUpdatedBy();

                interface AppInfo {
                        Long getId();

                        String getName();

                        int getEnvironment();

                }

                interface UserInfo {
                        Long getId();

                        String getName();
                }
        }

        @EntityGraph(attributePaths = { "app", "createdAt", "updatedAt" })
        Page<FlagItem> findByNameContainingIgnoreCase(String search, Pageable pageable);

        boolean existsByApp_IdAndFlagKey(Long appId, String flagKey);

        @Modifying(clearAutomatically = true)
        @Query("""
                        UPDATE AppFlags f SET f.currentVersion = :newVersion, f.updatedAt = :now , f.updatedBy = :userId
                        WHERE f.id = :id AND f.currentVersion = :expectedVersion
                        """)
        int insertVersion(@Param("id") Long id,
                        @Param("expectedVersion") int expectedVersion,
                        @Param("newVersion") int newVersion,
                        @Param("now") Instant now, @Param("userId") AppUser userId);

        @Modifying
        @Query("""
                        UPDATE AppFlags f SET f.enabled = NOT f.enabled,f.updatedBy = :userId, f.updatedAt = CURRENT_TIMESTAMP WHERE f.id = :id
                              """)
        int statusToggle(@Param("id") Long id, @Param("userId") AppUser userId);

        @Modifying
        @Query("DELETE FROM AppFlags f WHERE f.id = :id")
        int deleteFlag(@Param("id") Long id);

}
