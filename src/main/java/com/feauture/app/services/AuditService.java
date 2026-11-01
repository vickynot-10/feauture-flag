package com.feauture.app.services;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.feauture.app.dto.ApiRequest;
import com.feauture.app.dto.ApiResponse;
import com.feauture.app.repository.AppAuditLogRepository;
import com.feauture.app.repository.AppAuditLogRepository.AuditItem;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AppAuditLogRepository appAuditLogRepository;

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

}