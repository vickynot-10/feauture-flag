package com.feauture.app.dto;

import java.time.Instant;

public record AuditDiffResponse(
        Long flagId,
        String flagKey,
        VersionDetail from,
        VersionDetail to) {

    public record VersionDetail(
            int version,
            Instant createdAt,
            String createdBy,
            FlagDefinitionRequest.Definition definition) {
    }
}