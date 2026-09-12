package com.histdata.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

@Service
public class DownloadAuditService {
    private final DownloadAuditRepository repository;

    public DownloadAuditService(DownloadAuditRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long started(String username, String segment, LocalDate tradeDate, String key, String fileName, long sizeBytes) {
        DownloadAudit audit = new DownloadAudit();
        audit.setUsername(username);
        audit.setSegment(segment);
        audit.setTradeDate(tradeDate);
        audit.setObjectKey(key);
        audit.setFileName(fileName);
        audit.setSizeBytes(sizeBytes);
        audit.setStartedAt(Instant.now());
        audit.setStatus("STARTED");
        return repository.save(audit).getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void succeeded(Long auditId) {
        DownloadAudit audit = repository.findById(auditId).orElseThrow();
        audit.setStatus("SUCCESS");
        audit.setCompletedAt(Instant.now());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failed(Long auditId, Exception exception) {
        DownloadAudit audit = repository.findById(auditId).orElseThrow();
        audit.setStatus("FAILED");
        audit.setCompletedAt(Instant.now());
        String message = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
        audit.setFailureReason(message.substring(0, Math.min(message.length(), 500)));
    }
}

