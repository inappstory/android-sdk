package com.inappstory.sdk.lrudiskcache;

import android.os.Handler;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.impl.IASSettingsImpl;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.UUID;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CacheJournalTest {

    @Mock private FileManager fileManager;
    @Mock private ExceptionManager exceptionManager;
    @Mock private IASCore core;
    @Mock private Handler mockHandler;
    @Mock private IASSettingsImpl settingsImpl;

    @TempDir File tempDir;

    private CacheJournal journal;
    private File realJournalFile;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        realJournalFile = new File(tempDir, CacheJournal.JOURNAL_NAME);
        when(fileManager.getJournalFile()).thenReturn(realJournalFile);

        when(settingsImpl.sessionIdOrEmpty()).thenReturn("test-session-id");
        doReturn(settingsImpl).when(core).settingsAPI();

        configureSynchronousHandlerMock();

        journal = new CacheJournal(fileManager, exceptionManager, mockHandler);
    }

    private void configureSynchronousHandlerMock() {
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                Runnable runnable = invocation.getArgument(0);
                if (runnable != null) {
                    runnable.run();
                }
                return null;
            }
        }).when(mockHandler).postDelayed(any(Runnable.class), anyLong());

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                return null;
            }
        }).when(mockHandler).removeCallbacks(any(Runnable.class));
    }

    @Test
    void testPut_AddsItemAndUpdatesSize() throws Exception {
        CacheJournalItem item = createTestItem("key1", 500L, 1000L);

        journal.put(item, MB_1);

        assertEquals(item, journal.get("key1"));
        assertEquals(500L, getCurrentSize());
    }

    @Test
    void testPut_TriggersEvictionWhenLimitExceeded() throws Exception {
        CacheJournalItem item1 = createTestItem("key1", 500L, 1000L);
        CacheJournalItem item2 = createTestItem("key2", 100L, 2000L);
        CacheJournalItem item3 = createTestItem("key3", 401L, 3000L);

        journal.put(item1, 1000L);
        journal.put(item2, 1000L);

        verify(fileManager, never()).delete(anyString());
        assertEquals(600L, getCurrentSize());

        journal.put(item3, 1000L);

        verify(fileManager, times(1)).delete(anyString());
        assertEquals(501L, getCurrentSize());
        assertNotNull(journal.get("key3"));
    }

    @Test
    void testDelete_RemovesItemAndUpdatesSize() throws Exception {
        CacheJournalItem item = createTestItem("key1", 500L, 1000L);
        journal.put(item, MB_1);

        boolean result = journal.delete("key1", true);

        assertTrue(result);
        verify(fileManager).delete(item.getFilePath());
        assertNull(journal.get("key1"));
        assertEquals(0L, getCurrentSize());
    }

    @Test
    void testDelete_WithMimeType_RemovesOnlyMatchingType() throws Exception {
        CacheJournalItem itemVideo = createTestItem("key1", 500L, 1000L, "video/mp4");
        CacheJournalItem itemImage = createTestItem("key1", 300L, 2000L, "image/jpeg");

        journal.put(itemVideo, MB_1);
        journal.put(itemImage, MB_1);

        boolean result = journal.delete("key1", "video/mp4", true);

        assertTrue(result);
        verify(fileManager).delete(itemVideo.getFilePath());
        verify(fileManager, never()).delete(itemImage.getFilePath());

        assertNull(journal.get("key1", "video/mp4"));
        assertNotNull(journal.get("key1", "image/jpeg"));
    }

    @Test
    void testGet_UpdatesTimeAndSchedulesWrite() throws Exception {
        CacheJournalItem item = createTestItem("key1", 500L, 1000L);
        journal.put(item, MB_1);

        long initialTime = item.getTime();

        journal.get("key1");

        assertTrue(item.getTime() > initialTime);
        verify(mockHandler).removeCallbacks(any(Runnable.class));
        verify(mockHandler).postDelayed(any(Runnable.class), anyLong());
    }

    @Test
    void testWriteAndReadJournal_PersistsData() throws Exception {
        CacheJournalItem item = createTestItem("persistKey", 1024L, 5000L, "application/pdf");
        journal.put(item, MB_1);

        journal.forceWriteJournal();

        assertTrue(realJournalFile.exists());
        assertTrue(realJournalFile.length() > 0);

        CacheJournal newJournal = new CacheJournal(fileManager, exceptionManager, mockHandler);
        CacheJournalItem retrievedItem = newJournal.get("persistKey");

        assertNotNull(retrievedItem);
        assertEquals("persistKey", retrievedItem.getUniqueKey());
        assertEquals("application/pdf", retrievedItem.getType());
        assertEquals(1024L, retrievedItem.getSize());
    }

    @Test
    void testRemoveOld_GeneratesLogOnOverflow() throws Exception {
        CacheJournalItem item1 = createTestItem("key1", 600L, 1000L);
        CacheJournalItem item2 = createTestItem("key2", 600L, 2000L);

        journal.put(item1, 1000L);
        journal.put(item2, 1000L);

        // Verify that the exception manager was called to log the cache overflow
        verify(exceptionManager, times(1)).createExceptionLog(any(CacheOverflowError.class));
    }

    @Test
    void testPut_IgnoresItemLargerThanCacheSize() throws Exception {
        CacheJournalItem largeItem = createTestItem("largeKey", 2000L, 1000L);
        long initialSize = getCurrentSize();

        journal.put(largeItem, 1000L);

        assertNull(journal.get("largeKey"));
        assertEquals(initialSize, getCurrentSize());
        verify(fileManager, never()).delete(anyString());
    }

    @Test
    void testGet_ReturnsNullForNonExistentKey() {
        assertNull(journal.get("nonExistentKey"));
        assertNull(journal.get("nonExistentKey", "image/jpeg"));
    }

    @Test
    void testDelete_ReturnsFalseForNonExistentKey() throws Exception {
        boolean result = journal.delete("nonExistentKey", true);

        assertFalse(result);
        verify(fileManager, never()).delete(anyString());
    }

    @Test
    void testDelete_ReturnsFalseWhenMimeTypeDoesNotMatch() throws Exception {
        CacheJournalItem item = createTestItem("key1", 500L, 1000L, "video/mp4");
        journal.put(item, MB_1);

        boolean result = journal.delete("key1", "image/jpeg", true);

        assertFalse(result);
        verify(fileManager, never()).delete(anyString());
        assertNotNull(journal.get("key1", "video/mp4"));
    }

    @Test
    void testConstructor_HandlesEmptyJournalFileGracefully() throws Exception {
        when(fileManager.getJournalFile()).thenReturn(realJournalFile);
        assertTrue(realJournalFile.createNewFile());
        assertEquals(0, realJournalFile.length());

        CacheJournal newJournal = new CacheJournal(fileManager, exceptionManager, mockHandler);

        assertNull(newJournal.get("anyKey"));
        assertEquals(0L, getCurrentSizeFromInstance(newJournal));
    }

    @Test
    void testConstructor_HandlesInvalidJournalVersionGracefully() throws Exception {
        when(fileManager.getJournalFile()).thenReturn(realJournalFile);

        try (java.io.DataOutputStream stream = new java.io.DataOutputStream(new java.io.FileOutputStream(realJournalFile))) {
            stream.writeShort(99);
            stream.writeInt(1);
        }

        CacheJournal newJournal = new CacheJournal(fileManager, exceptionManager, mockHandler);

        assertNull(newJournal.get("anyKey"));
        assertEquals(0L, getCurrentSizeFromInstance(newJournal));
    }

    @Test
    void testWriteJournal_HandlesIOExceptionGracefully() throws Exception {
        File directoryAsJournal = new File(tempDir, "dir_as_journal");
        directoryAsJournal.mkdir();
        when(fileManager.getJournalFile()).thenReturn(directoryAsJournal);

        CacheJournalItem item = createTestItem("key1", 500L, 1000L);
        journal.put(item, MB_1);

        assertDoesNotThrow(new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                journal.forceWriteJournal();
            }
        });
    }

    @Test
    void testReadJournal_HandlesCorruptedDataGracefully() throws Exception {
        when(fileManager.getJournalFile()).thenReturn(realJournalFile);

        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(realJournalFile)) {
            fos.write(new byte[]{1, 2, 3, 4, 5});
        }

        CacheJournal newJournal = new CacheJournal(fileManager, exceptionManager, mockHandler);

        assertNull(newJournal.get("anyKey"));
        assertEquals(0L, getCurrentSizeFromInstance(newJournal));
    }

    private long getCurrentSizeFromInstance(CacheJournal instance) throws Exception {
        java.lang.reflect.Field field = CacheJournal.class.getDeclaredField("currentSize");
        field.setAccessible(true);
        return field.getLong(instance);
    }

    private long getCurrentSize() throws Exception {
        java.lang.reflect.Field field = CacheJournal.class.getDeclaredField("currentSize");
        field.setAccessible(true);
        return field.getLong(journal);
    }

    private CacheJournalItem createTestItem(String key, long size, long time) throws Exception {
        return createTestItem(key, size, time, "application/octet-stream");
    }

    private CacheJournalItem createTestItem(String key, long size, long time, String mimeType) throws Exception {
        String safeMimeType = mimeType != null ? mimeType.replace("/", "_") : "unknown";
        File dummyFile = new File(tempDir, key + "_" + safeMimeType + ".dat");

        if (!dummyFile.exists()) {
            dummyFile.createNewFile();
        }

        return new CacheJournalItem(
                key,
                dummyFile.getAbsolutePath(),
                "dat",
                mimeType,
                UUID.randomUUID().toString(),
                "",
                time,
                size,
                size,
                mimeType
        );
    }
}