package com.feauture.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.feauture.app.entity.AppFlagVersions;

public interface AppFlagVersionRepository extends JpaRepository<AppFlagVersions, Long> {
    Optional<AppFlagVersions> findByFlag_IdAndVersion(Long flagId, Integer version);

    @Query(value = """
            SELECT jsonb_build_object(
                'salt', v.definition->'salt',
                'bucketBy', v.definition->'bucketBy',
                'defaultOn', v.definition->'defaultOn',
                'conditions', (
                    SELECT jsonb_object_agg(
                        condition->>'attribute',
                        condition->'values'
                    )
                    FROM jsonb_array_elements(v.definition->'rules') AS rule
                    CROSS JOIN LATERAL jsonb_array_elements(rule->'conditions') AS condition
                )
            )::text
            FROM flag_versions v
            WHERE v.fk_flag_id = :flagId
            AND v.version = :version
            """, nativeQuery = true)
    String findConditionsByFlagIdAndVersion(Long flagId, Integer version);

}
