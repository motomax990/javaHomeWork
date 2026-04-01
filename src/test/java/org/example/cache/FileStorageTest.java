package org.example.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageTest {

    private FileStorage storage;
    private static final String TEST_CACHE_DIR = "test_cache";

    @BeforeEach
    void setUp() {
        storage = new FileStorage(TEST_CACHE_DIR);
    }

    @AfterEach
    void tearDown() {
        File dir = new File(TEST_CACHE_DIR);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        dir.delete();
    }

    @Test
    void saveAndLoadReturnsCorrectData() throws IOException {
        String data = "{\"name\": \"FalconSat\"}";
        storage.save("test.json", data);

        String loaded = storage.load("test.json");
        assertEquals(data, loaded);
    }

    @Test
    void loadReturnsNullForNonExistentFile() throws IOException {
        String result = storage.load("nonexistent.json");
        assertNull(result);
    }

    @Test
    void saveOverwritesExistingFile() throws IOException {
        storage.save("test.json", "old data");
        storage.save("test.json", "new data");

        String loaded = storage.load("test.json");
        assertEquals("new data", loaded);
    }

    @Test
    void isValidReturnsTrueForFreshCache() throws IOException {
        storage.save("test.json", "data");
        assertTrue(storage.isValid("test.json"));
    }

    @Test
    void isValidReturnsFalseForNonExistentKey() {
        assertFalse(storage.isValid("nonexistent.json"));
    }

    @Test
    void existsReturnsTrueAfterSave() throws IOException {
        storage.save("test.json", "data");
        assertTrue(storage.exists("test.json"));
    }

    @Test
    void existsReturnsFalseForNonExistentFile() {
        assertFalse(storage.exists("nonexistent.json"));
    }

    @Test
    void clearCacheRemovesAllFiles() throws IOException {
        storage.save("file1.json", "data1");
        storage.save("file2.json", "data2");

        storage.clearCache();

        assertNull(storage.load("file1.json"));
        assertNull(storage.load("file2.json"));
    }

    @Test
    void getSavedTimeReturnsNonNullAfterSave() throws IOException {
        storage.save("test.json", "data");
        String time = storage.getSavedTime("test.json");
        assertNotNull(time);
    }

    @Test
    void getSavedTimeReturnsNullForUnknownKey() {
        String time = storage.getSavedTime("unknown.json");
        assertNull(time);
    }

    @Test
    void clearCacheThenIsValidReturnsFalse() throws IOException {
        storage.save("test.json", "data");
        assertTrue(storage.isValid("test.json"));

        storage.clearCache();
        assertFalse(storage.isValid("test.json"));
    }

    @Test
    void saveMultilineDataPreservesContent() throws IOException {
        String data = "line1\nline2\nline3";
        storage.save("multi.json", data);

        String loaded = storage.load("multi.json");
        assertEquals(data, loaded);
    }
}
