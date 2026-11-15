
package com.feauture.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feauture.app.dto.ApiRequest;
import com.feauture.app.dto.ApiResponse;
import com.feauture.app.dto.AuditDiffResponse;
import com.feauture.app.services.AuditService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/audit-logs")

public class AuditLogsController {

    final public AuditService auditService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getAuditLogs(@ModelAttribute ApiRequest.PageQuery query) {
        return auditService.getAuditLogs(query);
    }

    @GetMapping("/difference")
    public ApiResponse<AuditDiffResponse> getDiff(@RequestParam Long id) {
        return auditService.getDiff(id);
    }
}
