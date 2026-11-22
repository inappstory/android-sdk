package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.data.IDownloadResource;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.FileCheckerResult;
import com.inappstory.sdk.lrudiskcache.FileCheckerSuccess;
import com.inappstory.sdk.lrudiskcache.ILruCachesHolder;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.StringsUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

public class GameSplashUseCaseTest {

    @Mock
    private IASCore core;
    @Mock
    private ILruCachesHolder cachesHolder;
    @Mock
    private LruDiskCache infiniteCache;
    @Mock
    private FileChecker fileChecker;
    @Mock
    private IDownloadResource splashScreen;
    @Mock
    private FilesDownloader filesDownloader;

    @TempDir
    File tempDir;

    private static final String TEST_URL = "https://example.com/splash.png";
    private static final String MOCK_MD5 = "mocked_md5_hash";
    private static final String MOCK_EXT = ".png";
    private static final long MOCK_SIZE = 2048L;
    private static final String MOCK_SHA1 = "valid_sha1_hash";

    private String generatedFilePath;
    private MockedStatic<StringsUtils> stringsUtilsMock;
    private MockedStatic<InAppStoryManager> inAppStoryManagerMock;
    private MockedConstruction<FilePathCacheGenerator> filePathGeneratorMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Early initialization of the generated path before constructor is called
        generatedFilePath = new File(tempDir, "mock_splash.png").getAbsolutePath();

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

        inAppStoryManagerMock = Mockito.mockStatic(InAppStoryManager.class);
        inAppStoryManagerMock.when(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                InAppStoryManager.showDLog(anyString(), anyString());
            }
        }).thenAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                return null;
            }
        });

        filePathGeneratorMock = Mockito.mockConstruction(
                FilePathCacheGenerator.class,
                new MockedConstruction.MockInitializer<FilePathCacheGenerator>() {
                    @Override
                    public void prepare(FilePathCacheGenerator mock, MockedConstruction.Context context) throws Throwable {
                        when(mock.generate()).thenReturn(generatedFilePath);
                    }
                });

        when(core.cachesHolder()).thenReturn(cachesHolder);
        when(cachesHolder.getInfiniteCache()).thenReturn(infiniteCache);
        when(core.filesDownloader()).thenReturn(filesDownloader);

        when(splashScreen.url()).thenReturn(TEST_URL);
        when(splashScreen.size()).thenReturn(MOCK_SIZE);
        when(splashScreen.sha1()).thenReturn(MOCK_SHA1);
    }

    @AfterEach
    void tearDown() {
        if (stringsUtilsMock != null) stringsUtilsMock.close();
        if (inAppStoryManagerMock != null) inAppStoryManagerMock.close();
        if (filePathGeneratorMock != null) filePathGeneratorMock.close();
    }

    private GameSplashUseCase createUseCase() {
        return new GameSplashUseCase(core, fileChecker, splashScreen);
    }

    @Test
    void testGetFile_ReturnsValidCachedState() throws Exception {
        File cachedFile = new File(tempDir, "cached_splash.png");
        assertTrue(cachedFile.createNewFile());

        DownloadFileState cachedState = new DownloadFileState(cachedFile, MOCK_SIZE, MOCK_SIZE);
        when(infiniteCache.get(MOCK_MD5)).thenReturn(cachedState);

        GameSplashUseCase useCase = createUseCase();
        DownloadFileState result = useCase.getFile();

        assertEquals(cachedState, result);
        verify(filesDownloader, never()).downloadFile(any(), any(), any(), any(), any(), any());
    }

    @Test
    void testGetFile_SuccessfulSynchronousDownloadAndCacheUpdate() throws Exception {
        when(infiniteCache.get(MOCK_MD5)).thenReturn(null);

        File downloadedFile = new File(tempDir, "downloaded_splash.png");
        assertTrue(downloadedFile.createNewFile());

        DownloadFileState downloadedState = new DownloadFileState(downloadedFile, MOCK_SIZE, MOCK_SIZE);
        FileCheckerSuccess successResult = mock(FileCheckerSuccess.class);

        when(fileChecker.checkWithShaAndSize(eq(downloadedFile), eq(MOCK_SIZE), eq(MOCK_SHA1), eq(true)))
                .thenReturn(successResult);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                FinishDownloadFileCallback callback = invocation.getArgument(5);
                callback.finish(downloadedState);
                return null;
            }
        }).when(filesDownloader).downloadFile(anyString(), any(File.class), isNull(), any(), isNull(), any());

        GameSplashUseCase useCase = createUseCase();
        DownloadFileState result = useCase.getFile();

        verify(infiniteCache).put(any());
        assertEquals(downloadedState, result);
    }

    @Test
    void testGetFile_HandlesInterruptedDownload() throws Exception {
        when(infiniteCache.get(MOCK_MD5)).thenReturn(null);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                FinishDownloadFileCallback callback = invocation.getArgument(5);
                callback.finish(null);
                return null;
            }
        }).when(filesDownloader).downloadFile(anyString(), any(File.class), isNull(), any(), isNull(), any());

        GameSplashUseCase useCase = createUseCase();
        DownloadFileState result = useCase.getFile();

        assertNull(result);
        verify(infiniteCache, never()).put(any());
    }


    @Test
    void testGetFile_HandlesValidationFailure() throws Exception {
        when(infiniteCache.get(MOCK_MD5)).thenReturn(null);

        File downloadedFile = new File(tempDir, "corrupted_splash.png");
        assertTrue(downloadedFile.createNewFile());

        DownloadFileState downloadedState = new DownloadFileState(downloadedFile, MOCK_SIZE, MOCK_SIZE);
        FileCheckerResult errorResult = mock(FileCheckerResult.class);

        when(fileChecker.checkWithShaAndSize(eq(downloadedFile), eq(MOCK_SIZE), eq(MOCK_SHA1), eq(true)))
                .thenReturn(errorResult);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                FinishDownloadFileCallback callback = invocation.getArgument(5);
                callback.finish(downloadedState);
                return null;
            }
        }).when(filesDownloader).downloadFile(anyString(), any(File.class), isNull(), any(), isNull(), any());

        GameSplashUseCase useCase = createUseCase();
        DownloadFileState result = useCase.getFile();

        assertNull(result);
        verify(infiniteCache, never()).put(any());
    }


    @Test
    void testGetFile_HandlesDownloaderException() throws Exception {
        when(infiniteCache.get(MOCK_MD5)).thenReturn(null);
        doThrow(new RuntimeException("Network error")).when(filesDownloader).downloadFile(
                anyString(), any(File.class), isNull(), any(), isNull(), any()
        );

        GameSplashUseCase useCase = createUseCase();
        DownloadFileState result = useCase.getFile();

        assertNull(result);
        inAppStoryManagerMock.verify(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                InAppStoryManager.showDLog(eq(LoggerTags.IAS_WARN_TAG), anyString());
            }
        }, times(1));
    }

    @Test
    void testGetFile_HandlesCacheJournalUpdateException() throws Exception {
        when(infiniteCache.get(MOCK_MD5)).thenReturn(null);
        doThrow(new RuntimeException("Disk full")).when(infiniteCache).put(any());

        File downloadedFile = new File(tempDir, "downloaded_splash.png");
        assertTrue(downloadedFile.createNewFile());

        DownloadFileState downloadedState = new DownloadFileState(downloadedFile, MOCK_SIZE, MOCK_SIZE);
        FileCheckerSuccess successResult = mock(FileCheckerSuccess.class);

        when(fileChecker.checkWithShaAndSize(eq(downloadedFile), eq(MOCK_SIZE), eq(MOCK_SHA1), eq(true)))
                .thenReturn(successResult);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                FinishDownloadFileCallback callback = invocation.getArgument(5);
                callback.finish(downloadedState);
                return null;
            }
        }).when(filesDownloader).downloadFile(anyString(), any(File.class), isNull(), any(), isNull(), any());

        GameSplashUseCase useCase = createUseCase();
        DownloadFileState result = useCase.getFile();

        assertNotNull(result);
        assertEquals(downloadedState, result);

        inAppStoryManagerMock.verify(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                InAppStoryManager.showDLog(eq(LoggerTags.IAS_WARN_TAG), anyString());
            }
        }, times(1));
    }
}