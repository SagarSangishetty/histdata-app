package com.histdata.storage;

import java.time.LocalDate;
import java.util.Set;

public final class StorageKey {
    private static final Set<String> SEGMENTS = Set.of("CM", "CD", "FO");
    private static final String SAFE_FILE_NAME = "[A-Za-z0-9][A-Za-z0-9._-]*\\.dat";

    private StorageKey() {
    }

    public static String prefix(String segment, LocalDate tradeDate) {
        String normalized = normalizeSegment(segment);
        return normalized + "/" + tradeDate + "/";
    }

    public static String validateObjectKey(String key) {
        if (key == null || key.isBlank() || key.startsWith("/") || key.contains("..") || key.contains("\\")) {
            throw new IllegalArgumentException("Invalid object key");
        }
        String[] parts = key.split("/");
        if (parts.length != 3 || !parts[2].matches(SAFE_FILE_NAME)) {
            throw new IllegalArgumentException("Object key must be SEGMENT/yyyy-MM-dd/file.dat");
        }
        normalizeSegment(parts[0]);
        LocalDate.parse(parts[1]);
        return key;
    }

    public static String normalizeSegment(String segment) {
        String normalized = segment == null ? "" : segment.toUpperCase();
        if (!SEGMENTS.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported segment");
        }
        return normalized;
    }
}
