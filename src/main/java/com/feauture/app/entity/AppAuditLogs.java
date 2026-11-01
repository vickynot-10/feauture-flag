package com.feauture.app.entity;

import java.time.Instant;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Immutable
@Table(name = "audit_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppAuditLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flag_key", nullable = false, updatable = false)
    private String flagKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private AuditAction action;

    @Column(name = "actor_id", nullable = false, updatable = false)
    private String actorId;

    @Column(name = "from_version", updatable = false)
    private Integer fromVersion;

    @Column(name = "to_version", updatable = false)
    private Integer toVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public AppAuditLogs(String flagKey, AuditAction action, String actorId, Integer fromVersion, Integer toVersion) {
        this.flagKey = flagKey;
        this.action = action;
        this.actorId = actorId;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }
}