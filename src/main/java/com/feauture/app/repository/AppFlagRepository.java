package com.feauture.app.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.feauture.app.entity.AppFlags;
import com.feauture.app.entity.AppUser;

public interface AppFlagRepository extends JpaRepository<AppFlags, Long>, JpaSpecificationExecutor<AppFlags> {

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
