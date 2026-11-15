package com.feauture.app.services;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.feauture.app.dto.ApiRequest;
import com.feauture.app.dto.ApiResponse;
import com.feauture.app.dto.AuditDiffResponse;
import com.feauture.app.entity.AppAuditLogs;
import com.feauture.app.entity.AppFlagVersions;
import com.feauture.app.entity.AuditAction;
import com.feauture.app.repository.AppAuditLogRepository;
import com.feauture.app.repository.AppAuditLogRepository.AuditItem;
import com.feauture.app.repository.AppFlagVersionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AppAuditLogRepository appAuditLogRepository;
    private final AppFlagVersionRepository appFlagVersionRepository;

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> getAuditLogs(ApiRequest.PageQuery query) {
        Pageable page = PageRequest.of(query.page() - 1, query.limit());
        String search = query.search() == null ? "" : query.search().trim();

        Page<AuditItem> result = appAuditLogRepository.findAuditItems(search, page);
        Map<String, Object> data = Map.of(
                "list", result.getContent(),
                "total", result.getTotalElements());
        return ApiResponse.success("Fetched Successfully !", data);
    }

    @Transactional(readOnly = true)
    public ApiResponse<AuditDiffResponse> getDiff(Long id) {
        AppAuditLogs log = appAuditLogRepository.findById(id)
                .orElseThrow(() -> ApiResponse.error(HttpStatus.NOT_FOUND, "Audit log not found !"));

        if (log.getAction() != AuditAction.UPDATE || log.getFromVersion() == null || log.getToVersion() == null) {
            throw ApiResponse.error(HttpStatus.BAD_REQUEST, "Only update logs have a difference !");
        }

        List<AppFlagVersions> found = appFlagVersionRepository.findForDiff(
                log.getFlagId(), new HashSet<>(List.of(log.getFromVersion(), log.getToVersion())));

        Map<Integer, AppFlagVersions> byVersion = found.stream()
                .collect(Collectors.toMap(AppFlagVersions::getVersion, Function.identity()));

        AppFlagVersions from = byVersion.get(log.getFromVersion());
        AppFlagVersions to = byVersion.get(log.getToVersion());

        if (from == null || to == null) {
            throw ApiResponse.error(HttpStatus.NOT_FOUND, "Versions not found, the flag may have been deleted !");
        }

        AuditDiffResponse response = new AuditDiffResponse(
                log.getFlagId(),
                log.getFlagKey(),
                toDetail(from),
                toDetail(to));

        return ApiResponse.success("Fetched Successfully !", response);
    }

    private AuditDiffResponse.VersionDetail toDetail(AppFlagVersions v) {
        return new AuditDiffResponse.VersionDetail(
                v.getVersion(),
                v.getCreatedAt(),
                v.getCreatedBy().getName(),
                v.getDefinition());
    }

}