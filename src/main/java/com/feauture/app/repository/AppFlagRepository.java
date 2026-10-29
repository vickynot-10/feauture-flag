package com.feauture.app.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.feauture.app.dto.FlagDefinitionRequest;
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

        interface FlagDetails {
                Long getId();

                String getName();

                String getFlagKey();

                int getCurrentVersion();

                Instant getCreatedAt();

                Instant getUpdatedAt();

                String getCreatedBy();

                String getLastUpdatedBy();

                String getAppName();

                int getAppEnvironment();

                FlagDefinitionRequest.Definition getDefinition();
        }

        @Query("""
                                SELECT f.id as id ,
                                f.name as name,
                                f.flagKey as flagKey,
                                f.currentVersion as currentVersion,
                                f.createdAt as createdAt ,
                                f.updatedAt as updatedAt,
                                creator.name as createdBy,
                                updated.name as lastUpdatedBy,
                                app.name as appName ,
                                app.environment as appEnvironment,
                                v.definition as definition
                                FROM AppFlags f
                                LEFT JOIN f.createdBy as creator
                                LEFT JOIN f.updatedBy as updated
                                LEFT JOIN f.app as app
                                LEFT JOIN AppFlagVersions v ON v.flag = f AND v.version = f.currentVersion
                                WHERE f.id =:id

                        """)
        Optional<FlagDetails> findFlagById(@Param("id") Long id);

        boolean existsByApp_IdAndFlagKey(Long appId, String flagKey);

        public interface ListFlagDetails {
                Long getId();

                String getName();

                String getFlagKey();

                String getAppName();

                Integer getAppEnvironment();

                Integer[] getVersions();
        }

        @Query(value = """
                        SELECT f.id AS "id" ,
                        f.flag_key AS "flagKey",
                        f.name AS "name" ,
                        app.environment AS "appEnvironment" ,
                        app.name AS "appName",
                        ARRAY_AGG(v.version  ORDER BY v.version DESC)
                        FILTER(WHERE v.version IS NOT NULL) AS "versions"
                        from flags f
                        LEFT JOIN apps app ON app.id = f.fk_app_id
                        LEFT JOIN flag_versions v ON v.fk_flag_id = f.id
                        WHERE f.id IS NOT NULL
                        GROUP BY   f.id,
                            f.name,
                            f.flag_key,
                            app.name,
                            app.environment,
                            f.updated_at
                        ORDER BY f.updated_at DESC

                        """, nativeQuery = true)
        List<ListFlagDetails> findAllFlagVersions();

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
