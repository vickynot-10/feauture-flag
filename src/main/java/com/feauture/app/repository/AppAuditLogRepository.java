package com.feauture.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.feauture.app.entity.AppAuditLogs;

public interface AppAuditLogRepository extends JpaRepository<AppAuditLogs, Long> {

}
