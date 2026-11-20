package com.inappstory.sdk.lrudiskcache;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FileManagerTest {

    @Mock private DownloadFileState mockFileState;

    @TempDir File tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstructor_CreatesSubDirectorySuccessfully() throws Exception {
        String subPath = "test_cache";
        FileManager fileManager = new FileManager(tempDir, subPath);

        File expectedDir = new File(tempDir, subPath);
        assertTrue(expectedDir.exists());
        assertTrue(expectedDir.isDirectory());
        assertEquals(expectedDir, fileManager.getCacheDir());
    }

    @Test
    void testGetJournalFile_CreatesFileAndDirectory() throws Exception {
        String subPath = "journal_test";
        FileManager fileManager = new FileManager(tempDir, subPath);

        File journalFile = fileManager.getJournalFile();

        assertTrue(journalFile.exists());
        assertEquals(CacheJournal.JOURNAL_NAME, journalFile.getName());
    }

    @Test
    void testGetJournalFile_HandlesExceptionGracefully() throws Exception {
        FileManager fileManager = new FileManager(tempDir, "error_test");

        File fakeDir = new File(tempDir, "fake_dir_file");
        fakeDir.createNewFile();
        setPrivateField(fileManager, "cacheDir", fakeDir);

        try (MockedStatic<InAppStoryManager> mockedManager = Mockito.mockStatic(InAppStoryManager.class)) {
            mockedManager.when(new MockedStatic.Verification() {
                @Override
                public void apply() throws Throwable {
                    InAppStoryManager.showELog(anyString(), anyString());
                }
            }).thenAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) {
                    return null;
                }
            });

            File result = fileManager.getJournalFile();

            assertNotNull(result);
            mockedManager.verify(new MockedStatic.Verification() {
                @Override
                public void apply() throws Throwable {
                    InAppStoryManager.showELog(eq(LoggerTags.IAS_ERROR_TAG), anyString());
                }
            }, times(1));
        }
    }

    @Test
    void testPut_RenamesFileSuccessfully() throws Exception {
        FileManager fileManager = new FileManager(tempDir, "put_test");

        File sourceFile = new File(tempDir, "source.dat");
        assertTrue(sourceFile.createNewFile());
        assertTrue(sourceFile.exists());

        // Use an absolute path, exactly as LruDiskCache.put does
        File targetFile = new File(tempDir, "renamed.dat");
        String newName = targetFile.getAbsolutePath();

        File result = fileManager.put(sourceFile, newName);

        assertNotNull(result);
        assertEquals(targetFile.getName(), result.getName());
        assertTrue(result.exists());
        assertFalse(sourceFile.exists());
    }

    @Test
    void testPut_ThrowsExceptionOnFailure() throws Exception {
        FileManager fileManager = new FileManager(tempDir, "put_fail_test");
        File sourceFile = new File(tempDir, "source.dat");
        assertTrue(sourceFile.createNewFile());

        File mockCacheDir = mock(File.class);
        when(mockCacheDir.exists()).thenReturn(false);
        when(mockCacheDir.mkdirs()).thenReturn(false);
        setPrivateField(fileManager, "cacheDir", mockCacheDir);

        IOException exception = assertThrows(IOException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() throws Throwable {
                fileManager.put(sourceFile, "new_name.dat");
            }
        });

        assertTrue(exception.getMessage().contains("Unable to use file"));
    }

    @Test
    void testDelete_RemovesExistingFile() throws Exception {
        FileManager fileManager = new FileManager(tempDir, "delete_test");

        File targetDir = new File(tempDir, "delete_test");
        File targetFile = new File(targetDir, "to_delete.dat");

        assertTrue(targetFile.createNewFile());
        assertTrue(targetFile.exists());

        fileManager.delete("to_delete.dat");

        assertFalse(targetFile.exists());
    }

    @Test
    void testDeleteRecursive_HandlesNullAndFiles() {
        assertFalse(FileManager.deleteRecursive(null));

        File tempFile = new File(tempDir, "single_file.txt");
        try {
            assertTrue(tempFile.createNewFile());
            assertTrue(FileManager.deleteRecursive(tempFile));
            assertFalse(tempFile.exists());
        } catch (IOException e) {
            fail("Failed to create test file");
        }
    }

    @Test
    void testDeleteRecursive_RemovesDirectoryAndContents() throws Exception {
        File dir = new File(tempDir, "recursive_dir");
        assertTrue(dir.mkdir());

        File file1 = new File(dir, "file1.txt");
        File file2 = new File(dir, "file2.txt");
        assertTrue(file1.createNewFile());
        assertTrue(file2.createNewFile());

        assertTrue(FileManager.deleteRecursive(dir));

        assertFalse(dir.exists());
        assertFalse(file1.exists());
        assertFalse(file2.exists());
    }

    @Test
    void testDeleteFolderRecursive_RespectsExcludePattern() throws Exception {
        File dir = new File(tempDir, "exclude_dir");
        assertTrue(dir.mkdir());

        File keepFile = new File(dir, "journal.bin");
        File deleteFile = new File(dir, "data.dat");
        assertTrue(keepFile.createNewFile());
        assertTrue(deleteFile.createNewFile());

        FileManager.deleteFolderRecursive(dir, true, "journal.bin");

        assertTrue(keepFile.exists());
        assertFalse(deleteFile.exists());

        // On Windows, a directory cannot be deleted if it still contains files.
        // Since keepFile was not deleted, the root directory will likely still exist.
        // We assert the file behavior, not the root directory existence.
    }

    @Test
    void testGetStringFromFile_ReadsContentCorrectly() throws Exception {
        File testFile = new File(tempDir, "read_test.txt");
        String expectedContent = "Hello, FileManager!\nSecond line.";

        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(expectedContent);
        }

        String result = FileManager.getStringFromFile(testFile);

        assertTrue(result.contains("Hello, FileManager!"));
        assertTrue(result.contains("Second line."));
    }

    @Test
    void testGetAndExists_ChecksFilePresence() throws Exception {
        FileManager fileManager = new FileManager(tempDir, "check_test");
        String fileName = "check_me.dat";

        assertFalse(fileManager.exists(fileName));

        File retrievedFile = fileManager.get(fileName);
        assertTrue(retrievedFile.createNewFile());

        assertTrue(fileManager.exists(fileName));
        assertEquals(retrievedFile, fileManager.get(fileName));
    }

    @Test
    void testGetFullFile_DelegatesToState() {
        File expectedFile = new File(tempDir, "full_file.dat");
        when(mockFileState.getFullFile()).thenReturn(expectedFile);

        File result = FileManager.getFullFile(mockFileState);

        assertEquals(expectedFile, result);
        verify(mockFileState, times(1)).getFullFile();
    }

    @Test
    void testGetFullFile_ReturnsNullForNullState() {
        File result = FileManager.getFullFile(null);
        assertNull(result);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = FileManager.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}