package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.SimpleUseCaseError;
import com.inappstory.sdk.game.cache.UnzipUseCase;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.ILruCachesHolder;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.GameArchiveItem;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.ProgressCallback;
import com.inappstory.sdk.utils.StringsUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GameFolderUseCaseTest {

    @Mock
    private IASCore core;
    @Mock
    private ILruCachesHolder cachesHolder;
    @Mock
    private LruDiskCache infiniteCache;
    @Mock
    private FileChecker fileChecker;
    @Mock
    private UseCaseCallback<String> useCaseCallback;
    @Mock
    private ProgressCallback progressCallback;
    @Mock
    private GameArchiveItem mockArchiveItem;

    @TempDir
    File tempDir;

    private static final String TEST_URL = "https://example.com/game_archive.zip";
    private static final String MOCK_MD5 = "mocked_md5_hash";
    private static final String MOCK_EXT = ".zip";
    private static final String GAME_NAME = "test_game";

    private String generatedFolderPath;
    private MockedStatic<StringsUtils> stringsUtilsMock;
    private MockedConstruction<FilePathCacheGenerator> filePathGeneratorMock;
    private MockedConstruction<UnzipUseCase> unzipUseCaseMock;

    private boolean forceUnzipFailure = false;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        generatedFolderPath = new File(tempDir, GAME_NAME + File.separator + MOCK_MD5 + File.separator).getAbsolutePath();

        stringsUtilsMock = Mockito.mockStatic(StringsUtils.class);
        stringsUtilsMock.when(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                StringsUtils.md5(anyString());
            }
        }).thenReturn(MOCK_MD5);

        stringsUtilsMock.when(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                StringsUtils.getExtensionFromUrl(anyString());
            }
        }).thenReturn(MOCK_EXT);

        filePathGeneratorMock = Mockito.mockConstruction(FilePathCacheGenerator.class,
                new MockedConstruction.MockInitializer<FilePathCacheGenerator>() {
                    @Override
                    public void prepare(FilePathCacheGenerator mock, MockedConstruction.Context context) throws Throwable {
                        generatedFolderPath = new File(tempDir, GAME_NAME + File.separator + MOCK_MD5 + File.separator).getAbsolutePath();
                        when(mock.generate()).thenReturn(generatedFolderPath);
                    }
                });

        unzipUseCaseMock = Mockito.mockConstruction(UnzipUseCase.class,
                new MockedConstruction.MockInitializer<UnzipUseCase>() {
                    @Override
                    public void prepare(UnzipUseCase mock, MockedConstruction.Context context) throws Throwable {
                        when(mock.unzip(anyString(), any())).thenAnswer(new Answer<Boolean>() {
                            @Override
                            public Boolean answer(InvocationOnMock invocation) {
                                return !forceUnzipFailure;
                            }
                        });
                    }
                });

        when(core.cachesHolder()).thenReturn(cachesHolder);
        when(cachesHolder.getInfiniteCache()).thenReturn(infiniteCache);

        when(mockArchiveItem.item()).thenReturn("expected_file.txt");
        when(mockArchiveItem.sha1()).thenReturn("valid_sha1_hash");
    }

    @AfterEach
    void tearDown() {
        if (stringsUtilsMock != null) stringsUtilsMock.close();
        if (filePathGeneratorMock != null) filePathGeneratorMock.close();
        if (unzipUseCaseMock != null) unzipUseCaseMock.close();
        forceUnzipFailure = false;
    }

    private GameFolderUseCase createUseCase() {
        // Теперь FileChecker передается в конструктор, рефлексия не нужна
        return new GameFolderUseCase(
                core, TEST_URL, GAME_NAME, fileChecker, Collections.singletonList(mockArchiveItem), useCaseCallback, progressCallback
        );
    }

    @Test
    void testGetFile_CacheHitValidDirectory() throws Exception {
        File validDir = new File(tempDir, "cached_dir");
        assertTrue(validDir.mkdir());
        File expectedFile = new File(validDir, "expected_file.txt");
        assertTrue(expectedFile.createNewFile());

        when(fileChecker.getFileSHA1(expectedFile)).thenReturn("valid_sha1_hash");
        when(infiniteCache.get(MOCK_MD5, "Folder")).thenReturn(new DownloadFileState(validDir, 1000L, 1000L));

        GameFolderUseCase useCase = createUseCase();
        useCase.getFile();

        verify(progressCallback).onProgress(100, 100);
        verify(useCaseCallback).onSuccess(validDir.getAbsolutePath());
        verify(infiniteCache, never()).put(any(), anyString());
    }

    @Test
    void testGetFile_DiskHitValidDirectory() throws Exception {
        when(infiniteCache.get(MOCK_MD5, "Folder")).thenReturn(null);

        File targetDir = new File(generatedFolderPath);
        assertTrue(targetDir.mkdirs());
        File expectedFile = new File(targetDir, "expected_file.txt");
        assertTrue(expectedFile.createNewFile());

        when(fileChecker.getFileSHA1(expectedFile)).thenReturn("valid_sha1_hash");

        GameFolderUseCase useCase = createUseCase();
        useCase.getFile();

        verify(infiniteCache).put(any(), eq("Folder"));
        verify(progressCallback).onProgress(100, 100);
        verify(useCaseCallback).onSuccess(generatedFolderPath);
    }

    @Test
    void testGetFile_MissingArchiveFile() throws Exception {
        when(infiniteCache.get(MOCK_MD5, "Folder")).thenReturn(null);

        GameFolderUseCase useCase = createUseCase();
        useCase.getFile();

        verify(useCaseCallback).onError(argThat(error ->
                error instanceof SimpleUseCaseError &&
                        error.message().contains("Can't find archive")
        ));
        verify(infiniteCache, never()).put(any(), anyString());
    }

    @Test
    void testGetFile_UnzipFails() throws Exception {
        when(infiniteCache.get(MOCK_MD5, "Folder")).thenReturn(null);
        forceUnzipFailure = true;

        File targetDir = new File(generatedFolderPath);
        assertTrue(targetDir.mkdirs());

        File zipFile = new File(generatedFolderPath + MOCK_EXT);
        assertTrue(zipFile.createNewFile());

        GameFolderUseCase useCase = createUseCase();
        useCase.getFile();

        verify(useCaseCallback).onError(argThat(error ->
                error instanceof SimpleUseCaseError &&
                        error.message().contains("Failed to unarchive game")
        ));
        verify(infiniteCache, never()).put(any(), anyString());
    }

    @Test
    void testGetFile_UnzipSucceedsButValidationFailsMissingFile() throws Exception {
        when(infiniteCache.get(MOCK_MD5, "Folder")).thenReturn(null);

        File targetDir = new File(generatedFolderPath);
        assertTrue(targetDir.mkdirs());

        File zipFile = new File(generatedFolderPath + MOCK_EXT);
        assertTrue(zipFile.createNewFile());

        GameFolderUseCase useCase = createUseCase();
        useCase.getFile();

        verify(useCaseCallback).onError(argThat(error ->
                error instanceof SimpleUseCaseError &&
                        error.message().contains("missing from archive")
        ));
        verify(infiniteCache, never()).put(any(), anyString());
    }

    @Test
    void testGetFile_UnzipSucceedsButValidationFailsWrongSha1() throws Exception {
        when(infiniteCache.get(MOCK_MD5, "Folder")).thenReturn(null);

        File targetDir = new File(generatedFolderPath);
        assertTrue(targetDir.mkdirs());

        File zipFile = new File(generatedFolderPath + MOCK_EXT);
        assertTrue(zipFile.createNewFile());

        File expectedFile = new File(targetDir, "expected_file.txt");
        assertTrue(expectedFile.createNewFile());

        when(fileChecker.getFileSHA1(expectedFile)).thenReturn("wrong_sha1_hash");

        GameFolderUseCase useCase = createUseCase();
        useCase.getFile();

        verify(useCaseCallback).onError(argThat(error ->
                error instanceof SimpleUseCaseError &&
                        error.message().contains("has sha1") && error.message().contains("Expected sha1")
        ));
        verify(infiniteCache, never()).put(any(), anyString());
    }

    @Test
    void testGetFile_UnzipSucceedsAndValidationPasses() throws Exception {
        when(infiniteCache.get(MOCK_MD5, "Folder")).thenReturn(null);

        File targetDir = new File(generatedFolderPath);
        assertTrue(targetDir.mkdirs());

        File zipFile = new File(generatedFolderPath + MOCK_EXT);
        assertTrue(zipFile.createNewFile());

        File expectedFile = new File(targetDir, "expected_file.txt");
        assertTrue(expectedFile.createNewFile());

        when(fileChecker.getFileSHA1(expectedFile)).thenReturn("valid_sha1_hash");

        GameFolderUseCase useCase = createUseCase();
        useCase.getFile();

        verify(infiniteCache).put(any(), eq("Folder"));
        verify(progressCallback).onProgress(100, 100);
        verify(useCaseCallback).onSuccess(generatedFolderPath);
    }
}