package com.histdata.storage;

import java.io.InputStream;

public record FileContent(String fileName, long sizeBytes, InputStream inputStream) implements AutoCloseable {
    @Override
    public void close() throws Exception {
        inputStream.close();
    }
}

