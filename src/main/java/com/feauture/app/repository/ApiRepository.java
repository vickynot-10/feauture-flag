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

import com.feauture.app.entity.AppApiKeys;
import com.feauture.app.entity.AppApplications;
import com.feauture.app.entity.AppUser;

public interface ApiRepository extends JpaRepository<AppApiKeys, Long>, JpaSpecificationExecutor<AppApiKeys> {

        interface ApiKeyItem {
                Long getId();

                String getName();

                String getKeyPrefix();

                Boolean getStatus();

                Instant getCreatedAt();

                Instant getUpdatedAt();

                AppInfo getApp();

                UserInfo getCreatedBy();

                UserInfo getUpdatedBy();

                interface AppInfo {
                        Long getId();

                        String getName();

                        Integer getEnvironment();
                }

                interface UserInfo {
                        Long getId();

                        String getName();
                }
        }

        @EntityGraph(attributePaths = { "app", "createdBy", "updatedBy" })
        Page<ApiKeyItem> findByNameContainingIgnoreCase(String search, Pageable pageable);

        @Modifying
        @Query("""
                            DELETE FROM AppApiKeys a WHERE a.id = :id
                        """)
        int deleteApi(@Param("id") Long id);

        @Modifying
        @Query("""
                            UPDATE AppApiKeys a SET a.name = :name, a.app = :app, a.updatedAt = :updatedAt , a.updatedBy = :userID WHERE a.id = :id
                        """)
        int updateApp(@Param("id") Long id, @Param("name") String name, @Param("userID") AppUser userID,
                        @Param("app") AppApplications app,
                        @Param("updatedAt") Instant updatedAt);

        @Modifying
        @Query("""
                        UPDATE AppApiKeys a SET a.status = CASE WHEN a.status = true THEN false ELSE true END,a.updatedBy = :userID, a.updatedAt = :updatedAt WHERE a.id = :id
                        """)
        int updateStatusApi(@Param("id") Long id, @Param("userID") AppUser userID,
                        @Param("updatedAt") Instant updatedAt);

}
