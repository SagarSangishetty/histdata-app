package com.histdata.storage;

import com.histdata.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@Profile("aws")
@RequiredArgsConstructor
public class S3HistoricalDataStore implements HistoricalDataStore {
    private final S3Client s3Client;
    private final AppProperties properties;

    @Override
    public List<HistoricalFile> list(String segment, LocalDate tradeDate) throws IOException {
        String prefix = StorageKey.prefix(segment, tradeDate);
        try {
            return s3Client.listObjectsV2Paginator(ListObjectsV2Request.builder()
                            .bucket(properties.s3Bucket())
                            .prefix(prefix)
                            .build())
                    .contents().stream()
                    .filter(object -> !object.key().endsWith("/"))
                    .filter(object -> object.key().toLowerCase().endsWith(".dat"))
                    .map(object -> new HistoricalFile(object.key(), fileName(object.key()), object.size()))
                    .toList();
        } catch (S3Exception exception) {
            throw new IOException("Unable to list S3 historical files", exception);
        }
    }

    @Override
    public FileContent open(String key) throws IOException {
        String validKey = StorageKey.validateObjectKey(key);
        try {
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(properties.s3Bucket())
                    .key(validKey)
                    .build());
            return new FileContent(fileName(validKey), response.response().contentLength(), response);
        } catch (S3Exception exception) {
            throw new IOException("Unable to open S3 historical file", exception);
        }
    }

    private String fileName(String key) {
        return key.substring(key.lastIndexOf('/') + 1);
    }
}

