package com.histdata.storage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface HistoricalDataStore {
    List<HistoricalFile> list(String segment, LocalDate tradeDate) throws IOException;
    FileContent open(String key) throws IOException;
}

