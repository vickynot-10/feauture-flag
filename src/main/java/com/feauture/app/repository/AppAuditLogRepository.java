package com.feauture.app.repository;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.feauture.app.entity.AppAuditLogs;
import com.feauture.app.entity.AuditAction;

public interface AppAuditLogRepository extends JpaRepository<AppAuditLogs, Long> {

    interface AuditItem {
        Long getId();

        Long getFlagId();

        String getFlagKey();

        AuditAction getAction();

        Long getActorId();

        String getActorName();

        Integer getFromVersion();

        Integer getToVersion();

        Instant getCreatedAt();
    }

    @Query(value = """
            select a.id as id, a.flagId as flagId, a.flagKey as flagKey, a.action as action,
                   a.actorId as actorId, u.name as actorName,
                   a.fromVersion as fromVersion, a.toVersion as toVersion, a.createdAt as createdAt
            from AppAuditLogs a
            left join AppUser u on u.id = a.actorId
            where lower(a.flagKey) like lower(concat('%', :search, '%'))
            order by a.createdAt desc
            """, countQuery = """
            select count(a)
            from AppAuditLogs a
            where lower(a.flagKey) like lower(concat('%', :search, '%'))
            """)
    Page<AuditItem> findAuditItems(@Param("search") String search, Pageable pageable);
}