package com.histdata.storage;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StorageKeyTest {
    @Test
    void buildsExpectedPrefix() {
        assertEquals("CM/2026-09-12/", StorageKey.prefix("cm", LocalDate.of(2026, 9, 12)));
    }

    @Test
    void rejectsPathTraversal() {
        assertThrows(IllegalArgumentException.class,
                () -> StorageKey.validateObjectKey("CM/2026-09-12/../../secret.dat"));
    }

    @Test
    void acceptsExpectedDatKey() {
        assertEquals("FO/2026-09-12/fo_trade.dat",
                StorageKey.validateObjectKey("FO/2026-09-12/fo_trade.dat"));
    }

    @Test
    void rejectsUnsafeFileName() {
        assertThrows(IllegalArgumentException.class,
                () -> StorageKey.validateObjectKey("CM/2026-09-12/bad%0Aname.dat"));
    }
}
