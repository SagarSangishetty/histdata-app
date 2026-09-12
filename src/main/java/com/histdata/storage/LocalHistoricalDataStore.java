package com.histdata.storage;

import com.histdata.config.AppProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@Service
@Profile("local")
public class LocalHistoricalDataStore implements HistoricalDataStore {
    private final Path root;

    public LocalHistoricalDataStore(AppProperties properties) {
        this.root = Path.of(properties.localRoot()).toAbsolutePath().normalize();
    }

    @Override
    public List<HistoricalFile> list(String segment, LocalDate tradeDate) throws IOException {
        String prefix = StorageKey.prefix(segment, tradeDate);
        Path directory = safeResolve(prefix);
        if (!Files.isDirectory(directory)) {
            return List.of();
        }
        try (var paths = Files.list(directory)) {
            return paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".dat"))
                    .sorted()
                    .map(path -> toHistoricalFile(prefix, path))
                    .toList();
        }
    }

    @Override
    public FileContent open(String key) throws IOException {
        String validKey = StorageKey.validateObjectKey(key);
        Path path = safeResolve(validKey);
        if (!Files.isRegularFile(path)) {
            throw new IOException("Historical data file not found");
        }
        return new FileContent(path.getFileName().toString(), Files.size(path), Files.newInputStream(path));
    }

    private HistoricalFile toHistoricalFile(String prefix, Path path) {
        try {
            return new HistoricalFile(prefix + path.getFileName(), path.getFileName().toString(), Files.size(path));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read file metadata", exception);
        }
    }

    private Path safeResolve(String key) {
        Path resolved = root.resolve(key).normalize();
        if (!resolved.startsWith(root)) {
            throw new IllegalArgumentException("Invalid local data path");
        }
        return resolved;
    }
}

