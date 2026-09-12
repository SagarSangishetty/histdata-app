package com.histdata.web;

import com.histdata.audit.DownloadAuditService;
import com.histdata.storage.FileContent;
import com.histdata.storage.HistoricalDataStore;
import com.histdata.storage.HistoricalFile;
import com.histdata.storage.StorageKey;
import com.histdata.subscription.EntitlementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FileController {
    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

    private final HistoricalDataStore dataStore;
    private final EntitlementService entitlementService;
    private final DownloadAuditService auditService;

    @GetMapping("/files")
    String files(@RequestParam(defaultValue = "CM") String segment,
                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                 Authentication authentication,
                 Model model) {
        LocalDate selectedDate = date == null ? LocalDate.now() : date;
        String normalizedSegment = StorageKey.normalizeSegment(segment);
        List<HistoricalFile> files = List.of();
        String error = null;
        try {
            entitlementService.requireEntitlement(authentication.getName(), normalizedSegment, selectedDate);
            files = dataStore.list(normalizedSegment, selectedDate);
        } catch (Exception exception) {
            error = exception.getMessage();
            LOG.warn("file_list_failed user={} segment={} date={} reason={}",
                    authentication.getName(), normalizedSegment, selectedDate, exception.getMessage());
        }
        model.addAttribute("segment", normalizedSegment);
        model.addAttribute("date", selectedDate);
        model.addAttribute("files", files);
        model.addAttribute("error", error);
        return "files";
    }

    @GetMapping("/download")
    ResponseEntity<StreamingResponseBody> download(@RequestParam String key, Authentication authentication) throws IOException {
        String validKey = StorageKey.validateObjectKey(key);
        String[] parts = validKey.split("/");
        String segment = parts[0];
        LocalDate tradeDate = LocalDate.parse(parts[1]);
        entitlementService.requireEntitlement(authentication.getName(), segment, tradeDate);

        FileContent content = dataStore.open(validKey);
        Long auditId = auditService.started(authentication.getName(), segment, tradeDate,
                validKey, content.fileName(), content.sizeBytes());

        StreamingResponseBody body = outputStream -> streamAndAudit(content, outputStream, auditId, authentication.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(content.sizeBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(content.fileName()).build().toString())
                .body(body);
    }

    private void streamAndAudit(FileContent content, OutputStream outputStream, Long auditId, String username)
            throws IOException {
        try (content) {
            content.inputStream().transferTo(outputStream);
            outputStream.flush();
            auditService.succeeded(auditId);
            LOG.info("download_succeeded auditId={} user={} file={} bytes={}",
                    auditId, username, content.fileName(), content.sizeBytes());
        } catch (Exception exception) {
            auditService.failed(auditId, exception);
            LOG.error("download_failed auditId={} user={} file={} reason={}",
                    auditId, username, content.fileName(), exception.getMessage());
            if (exception instanceof IOException ioException) {
                throw ioException;
            }
            throw new IOException("Download failed", exception);
        }
    }
}
