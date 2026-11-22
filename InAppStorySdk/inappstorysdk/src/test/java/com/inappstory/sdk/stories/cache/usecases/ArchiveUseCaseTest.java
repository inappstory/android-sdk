package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASStatistic;
import com.inappstory.sdk.core.api.IASStatisticProfiling;
import com.inappstory.sdk.game.cache.GameNameHolder;
import com.inappstory.sdk.game.cache.SimpleUseCaseError;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.FileCheckerError;
import com.inappstory.sdk.lrudiskcache.FileCheckerResult;
import com.inappstory.sdk.lrudiskcache.ILruCachesHolder;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.ProgressCallback;
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

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ArchiveUseCaseTest {

    @Mock
    private IASCore core;
    @Mock
    private ILruCachesHolder cachesHolder;
    @Mock
    private LruDiskCache infiniteCache;
    @Mock
    private FileChecker fileChecker;
    @Mock
    private ProgressCallback progressCallback;
    @Mock
    private DownloadInterruption interruption;
    @Mock
    private UseCaseCallback<File> useCaseCallback;
    @Mock
    private FilesDownloader filesDownloader;
    @Mock
    private IASStatistic statisticAPI;
    @Mock
    private IASStatisticProfiling profilingAPI;
    @Mock
    private File cacheDir;
    private String gameName;
    private String generatedFilePath;

    @TempDir
    File tempDir;

    private static final String TEST_URL = "https://example.com/game_v2.zip";
    private static final String MOCK_MD5 = "mocked_md5_hash";
    private static final String MOCK_EXT = ".zip";

    private MockedStatic<StringsUtils> stringsUtilsMock;
    private MockedConstruction<FilePathCacheGenerator> filePathGeneratorMock;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        this.gameName = new GameNameHolder().getGameName(TEST_URL);

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

        when(core.cachesHolder()).thenReturn(cachesHolder);
        when(cachesHolder.getInfiniteCache()).thenReturn(infiniteCache);

        cacheDir = mock(File.class);
        when(cacheDir.getAbsolutePath()).thenReturn(tempDir.getAbsolutePath());
        when(cacheDir.getFreeSpace()).thenReturn(1024L * 1024L * 1024L);
        when(infiniteCache.getCacheDir()).thenReturn(cacheDir);

        String validPrefix = cacheDir.getAbsolutePath() + File.separator + "v2" + File.separator + "zip" + File.separator;

        filePathGeneratorMock = Mockito.mockConstruction(FilePathCacheGenerator.class,
                new MockedConstruction.MockInitializer<FilePathCacheGenerator>() {
                    @Override
                    public void prepare(FilePathCacheGenerator mock, MockedConstruction.Context context) throws Throwable {
                        generatedFilePath = validPrefix + "mock_archive_path.zip";
                        when(mock.generate()).thenReturn(generatedFilePath);
                    }
                }
        );

        when(core.statistic()).thenReturn(statisticAPI);
        when(statisticAPI.profiling()).thenReturn(profilingAPI);
        when(core.filesDownloader()).thenReturn(filesDownloader);
    }

    @AfterEach
    void tearDown() {
        if (stringsUtilsMock != null) {
            stringsUtilsMock.close();
        }
        if (filePathGeneratorMock != null) {
            filePathGeneratorMock.close();
        }
    }

    @Test
    void testGetFile_DownloadsWhenUseLocalFileIsFalse() throws Exception {
        ArchiveUseCase useCase = new ArchiveUseCase(
                core,
                TEST_URL,
                gameName,
                fileChecker,
                1000L,
                "sha1",
                500L,
                progressCallback,
                interruption,
                useCaseCallback,
                false
        );

        useCase.getFile();

        verify(infiniteCache).clearOldArchiveVersions(anyString(), eq(true));
        verify(infiniteCache).delete(any(), eq("Archive"));
        verify(filesDownloader).downloadFile(
                eq(TEST_URL), any(File.class), any(FileLoadProgressCallback.class),
                any(), eq(interruption), eq(0L), any(FinishDownloadFileCallback.class)
        );
    }

    @Test
    void testGetFile_UsesValidLocalArchive() {
        File mockCachedFile = new File(tempDir, "cached.zip");
        when(infiniteCache.getFullFile(MOCK_MD5, "Archive")).thenReturn(mockCachedFile);

        FileCheckerResult successResult = mock(FileCheckerResult.class);
        when(fileChecker.checkWithShaAndSize(eq(mockCachedFile), eq(1000L), eq("sha1"), eq(true)))
                .thenReturn(successResult);

        ArchiveUseCase useCase = new ArchiveUseCase(
                core,
                TEST_URL,
                gameName,
                fileChecker,
                1000L,
                "sha1",
                500L,
                progressCallback,
                interruption,
                useCaseCallback,
                true
        );

        useCase.getFile();

        verify(useCaseCallback).onSuccess(mockCachedFile);
        verify(filesDownloader, never()).downloadFile(any(), any(), any(), any(), any(), anyLong(), any());
    }

    @Test
    void testGetFile_DeletesCorruptedLocalArchive() throws Exception {
        File mockCachedFile = new File(tempDir, "corrupted.zip");
        when(infiniteCache.getFullFile(MOCK_MD5, "Archive")).thenReturn(mockCachedFile);
        when(fileChecker.checkWithShaAndSize(eq(mockCachedFile), eq(1000L), eq("sha1"), eq(true)))
                .thenReturn(new FileCheckerError("Invalid size"));

        ArchiveUseCase useCase = new ArchiveUseCase(
                core,
                TEST_URL,
                gameName,
                fileChecker,
                1000L,
                "sha1",
                500L,
                progressCallback,
                interruption,
                useCaseCallback,
                true
        );

        useCase.getFile();

        verify(infiniteCache).deleteArchiveAndExtractedFolder(MOCK_MD5, "Archive");
        verify(useCaseCallback).onError(any(SimpleUseCaseError.class));
    }

    @Test
    void testDownloadArchive_FailsOnInsufficientSpace() {
        doReturn(100L).when(cacheDir).getFreeSpace();

        ArchiveUseCase useCase = new ArchiveUseCase(
                core,
                TEST_URL,
                gameName,
                fileChecker,
                1000L,
                "sha1",
                5000L,
                progressCallback,
                interruption,
                useCaseCallback,
                false
        );

        useCase.getFile();

        verify(useCaseCallback).onError(argThat(error ->
                error instanceof SimpleUseCaseError &&
                        error.message().contains("No free space")
        ));
        verify(filesDownloader, never()).downloadFile(any(), any(), any(), any(), any(), anyLong(), any());
    }

    @Test
    void testFinishCallback_HandlesFullDownloadSuccess() throws Exception {
        ArchiveUseCase useCase = new ArchiveUseCase(
                core,
                TEST_URL,
                gameName,
                fileChecker,
                1000L,
                "sha1",
                500L,
                progressCallback,
                interruption,
                useCaseCallback,
                false
        );

        useCase.getFile();

        ArgumentCaptor<FinishDownloadFileCallback> callbackCaptor = ArgumentCaptor.forClass(FinishDownloadFileCallback.class);
        verify(filesDownloader).downloadFile(any(), any(), any(), any(), any(), anyLong(), callbackCaptor.capture());

        File downloadedFile = new File(tempDir, "downloaded.zip");
        downloadedFile.createNewFile();
        DownloadFileState state = new DownloadFileState(downloadedFile, 1000L, 1000L);

        when(fileChecker.checkWithShaAndSize(eq(downloadedFile), eq(1000L), eq("sha1"), eq(true)))
                .thenReturn(mock(FileCheckerResult.class));

        callbackCaptor.getValue().finish(state);

        verify(useCaseCallback).onSuccess(downloadedFile);
        verify(profilingAPI).setReady(anyString());
        verify(infiniteCache).put(any(), eq("Archive"));
    }

    @Test
    void testFinishCallback_HandlesFullDownloadWithCheckerError() throws Exception {
        ArchiveUseCase useCase = new ArchiveUseCase(
                core,
                TEST_URL,
                gameName,
                fileChecker,
                1000L,
                "sha1",
                500L,
                progressCallback,
                interruption,
                useCaseCallback,
                false
        );

        useCase.getFile();

        ArgumentCaptor<FinishDownloadFileCallback> callbackCaptor = ArgumentCaptor.forClass(FinishDownloadFileCallback.class);
        verify(filesDownloader).downloadFile(any(), any(), any(), any(), any(), anyLong(), callbackCaptor.capture());

        File downloadedFile = new File(tempDir, "downloaded.zip");
        downloadedFile.createNewFile();
        DownloadFileState state = new DownloadFileState(downloadedFile, 1000L, 1000L);

        when(fileChecker.checkWithShaAndSize(eq(downloadedFile), eq(1000L), eq("sha1"), eq(true)))
                .thenReturn(new FileCheckerError("SHA mismatch"));

        callbackCaptor.getValue().finish(state);

        verify(useCaseCallback).onError(argThat(error ->
                error instanceof SimpleUseCaseError &&
                        error.message().contains("sha or size is incorrect")
        ));
        verify(infiniteCache).put(any(), eq("Archive"));
    }

    @Test
    void testFinishCallback_HandlesInterruptedDownload() throws Exception {
        ArchiveUseCase useCase = new ArchiveUseCase(
                core,
                TEST_URL,
                gameName,
                fileChecker,
                1000L,
                "sha1",
                500L,
                progressCallback,
                interruption,
                useCaseCallback,
                false
        );

        useCase.getFile();

        ArgumentCaptor<FinishDownloadFileCallback> callbackCaptor = ArgumentCaptor.forClass(FinishDownloadFileCallback.class);
        verify(filesDownloader).downloadFile(any(), any(), any(), any(), any(), anyLong(), callbackCaptor.capture());

        callbackCaptor.getValue().finish(null);

        verify(useCaseCallback).onError(argThat(error ->
                error instanceof SimpleUseCaseError &&
                        error.message().contains("interrupted")
        ));
        verify(infiniteCache, never()).put(any(), any());
    }

    @Test
    void testProgressCallback_DelegatesToUserCallback() {
        ArchiveUseCase useCase = new ArchiveUseCase(
                core,
                TEST_URL,
                gameName,
                fileChecker,
                1000L,
                "sha1",
                500L,
                progressCallback,
                interruption,
                useCaseCallback,
                false
        );

        useCase.getFile();

        ArgumentCaptor<FileLoadProgressCallback> progressCaptor = ArgumentCaptor.forClass(FileLoadProgressCallback.class);
        verify(filesDownloader).downloadFile(any(), any(), progressCaptor.capture(), any(), any(), anyLong(), any());

        progressCaptor.getValue().onProgress(500L, 1000L);

        verify(progressCallback).onProgress(500L, 1000L);
    }

    @Test
    void testProgressCallback_HandlesDownloadError() {
        ArchiveUseCase useCase = new ArchiveUseCase(
                core,
                TEST_URL,
                gameName,
                fileChecker,
                1000L,
                "sha1",
                500L,
                progressCallback,
                interruption,
                useCaseCallback,
                false
        );

        useCase.getFile();

        ArgumentCaptor<FileLoadProgressCallback> progressCaptor = ArgumentCaptor.forClass(FileLoadProgressCallback.class);
        verify(filesDownloader).downloadFile(any(), any(), progressCaptor.capture(), any(), any(), anyLong(), any());

        progressCaptor.getValue().onError("Network timeout");

        verify(useCaseCallback).onError(argThat(error ->
                error instanceof SimpleUseCaseError &&
                        error.message().equals("Network timeout")
        ));
    }
}