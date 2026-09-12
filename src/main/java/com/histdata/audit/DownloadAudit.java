package com.histdata.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "DOWNLOAD_AUDIT", indexes = {
        @Index(name = "IX_AUDIT_USERNAME", columnList = "USERNAME"),
        @Index(name = "IX_AUDIT_STARTED_AT", columnList = "STARTED_AT")
})
@Getter
@Setter
@NoArgsConstructor
public class DownloadAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "download_audit_seq")
    @SequenceGenerator(name = "download_audit_seq", sequenceName = "DOWNLOAD_AUDIT_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 80)
    private String username;

    @Column(nullable = false, length = 10)
    private String segment;

    @Column(nullable = false)
    private LocalDate tradeDate;

    @Column(nullable = false, length = 255)
    private String objectKey;

    @Column(nullable = false, length = 160)
    private String fileName;

    private Long sizeBytes;

    @Column(nullable = false)
    private Instant startedAt;

    private Instant completedAt;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 500)
    private String failureReason;
}

