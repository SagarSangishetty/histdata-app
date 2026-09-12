package com.histdata.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DownloadAuditRepository extends JpaRepository<DownloadAudit, Long> {
    List<DownloadAudit> findTop100ByOrderByStartedAtDesc();
}

