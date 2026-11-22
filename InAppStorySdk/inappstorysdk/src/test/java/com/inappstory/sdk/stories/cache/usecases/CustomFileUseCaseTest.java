package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.SimpleUseCaseError;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.ILruCachesHolder;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

public class CustomFileUseCaseTest {

    @Mock
    private IASCore core;
    @Mock
    private ILruCachesHolder cachesHolder;
    @Mock
    private LruDiskCache infiniteCache;
    @Mock
    private FilesDownloader filesDownloader;
    @Mock
    private UseCaseCallback<File> getFileCallback;

    @TempDir
    File tempDir;

    private static final String TEST_URL = "https://example.com/custom_file.dat";
    private static final String MOCK_MD5 = "mocked_md5_hash";

    private File mockFile;
    private String mockFilePath;

    private MockedStatic<StringsUtils> stringsUtilsMock;
    private MockedConstruction<FilePathCacheGenerator> filePathGeneratorMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        mockFile = new File(tempDir, "custom_file.dat");
        mockFile.createNewFile();
        mockFilePath = mockFile.getAbsolutePath();

        stringsUtilsMock = Mockito.mockStatic(StringsUtils.class);
        stringsUtilsMock.when(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                StringsUtils.md5(anyString());
            }
        }).thenReturn(MOCK_MD5);

        filePathGeneratorMock = Mockito.mockConstruction(FilePathCacheGenerator.class,
                new MockedConstruction.MockInitializer<FilePathCacheGenerator>() {
                    @Override
                    public void prepare(FilePathCacheGenerator mock, MockedConstruction.Context context) throws Throwable {
                        when(mock.generate()).thenReturn(mockFilePath);
                    }
                });

        when(core.cachesHolder()).thenReturn(cachesHolder);
        when(cachesHolder.getInfiniteCache()).thenReturn(infiniteCache);
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
    void testGetFile_UsesLocalCopyWhenFullyDownloaded() {
        DownloadFileState fullState = new DownloadFileState(mockFile, 1000L, 1000L);
        when(infiniteCache.get(MOCK_MD5)).thenReturn(fullState);

        CustomFileUseCase useCase = new CustomFileUseCase(core, TEST_URL, getFileCallback);
        useCase.getFile();

        verify(getFileCallback).onSuccess(mockFile);
        verify(filesDownloader, never()).downloadFile(any(), any(), any(), any(), any(), anyLong(), any());
    }

    @Test
    void testGetFile_DownloadsWhenCacheMiss() throws Exception {
        when(infiniteCache.get(MOCK_MD5)).thenReturn(null);

        CustomFileUseCase useCase = new CustomFileUseCase(core, TEST_URL, getFileCallback);
        useCase.getFile();

        ArgumentCaptor<FinishDownloadFileCallback> callbackCaptor = ArgumentCaptor.forClass(FinishDownloadFileCallback.class);
        verify(filesDownloader).downloadFile(
                eq(TEST_URL), any(File.class), isNull(), any(), isNull(), anyLong(), callbackCaptor.capture()
        );

        DownloadFileState fullState = new DownloadFileState(mockFile, 1000L, 1000L);
        callbackCaptor.getValue().finish(fullState);

        verify(infiniteCache).put(any(CacheJournalItem.class));
        verify(getFileCallback).onSuccess(mockFile);
    }

    @Test
    void testGetFile_HandlesInterruptedDownload() throws Exception {
        when(infiniteCache.get(MOCK_MD5)).thenReturn(null);

        CustomFileUseCase useCase = new CustomFileUseCase(core, TEST_URL, getFileCallback);
        useCase.getFile();

        ArgumentCaptor<FinishDownloadFileCallback> callbackCaptor = ArgumentCaptor.forClass(FinishDownloadFileCallback.class);
        verify(filesDownloader).downloadFile(
                eq(TEST_URL), any(File.class), isNull(), any(), isNull(), anyLong(), callbackCaptor.capture()
        );

        callbackCaptor.getValue().finish(null);

        verify(getFileCallback).onError(argThat(error ->
                error instanceof SimpleUseCaseError &&
                        error.message().contains("interrupted or incomplete")
        ));
        verify(infiniteCache, never()).put(any());
    }

    @Test
    void testGetFile_HandlesDownloadInitiationFailure() {
        when(infiniteCache.get(MOCK_MD5)).thenReturn(null);

        doThrow(new RuntimeException("Network error")).when(filesDownloader).downloadFile(
                anyString(),
                any(File.class),
                isNull(),
                any(),
                isNull(),
                anyLong(),
                any()
        );

        try (MockedStatic<InAppStoryManager> mockedManager = Mockito.mockStatic(InAppStoryManager.class)) {
            mockedManager.when(new MockedStatic.Verification() {
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

            CustomFileUseCase useCase = new CustomFileUseCase(core, TEST_URL, getFileCallback);
            useCase.getFile();

            mockedManager.verify(new MockedStatic.Verification() {
                @Override
                public void apply() throws Throwable {
                    InAppStoryManager.showDLog(eq(LoggerTags.IAS_WARN_TAG), anyString());
                }
            }, times(1));

            verify(getFileCallback).onError(argThat(error ->
                    error instanceof SimpleUseCaseError &&
                            error.message().contains("Failed to start download")
            ));
        }
    }

    @Test
    void testGetFile_HandlesJournalUpdateFailure() throws Exception {
        when(infiniteCache.get(MOCK_MD5)).thenReturn(null);

        doThrow(new RuntimeException("Disk full")).when(infiniteCache).put(any(CacheJournalItem.class));

        try (MockedStatic<InAppStoryManager> mockedManager = Mockito.mockStatic(InAppStoryManager.class)) {
            mockedManager.when(new MockedStatic.Verification() {
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

            CustomFileUseCase useCase = new CustomFileUseCase(core, TEST_URL, getFileCallback);
            useCase.getFile();

            ArgumentCaptor<FinishDownloadFileCallback> callbackCaptor = ArgumentCaptor.forClass(FinishDownloadFileCallback.class);
            verify(filesDownloader).downloadFile(
                    eq(TEST_URL), any(File.class), isNull(), any(), isNull(), anyLong(), callbackCaptor.capture()
            );

            DownloadFileState fullState = new DownloadFileState(mockFile, 1000L, 1000L);
            callbackCaptor.getValue().finish(fullState);

            mockedManager.verify(new MockedStatic.Verification() {
                @Override
                public void apply() throws Throwable {
                    InAppStoryManager.showDLog(eq(LoggerTags.IAS_WARN_TAG), anyString());
                }
            }, times(1));

            verify(getFileCallback).onError(argThat(error ->
                    error instanceof SimpleUseCaseError &&
                            error.message().contains("Failed to save file metadata")
            ));
            verify(getFileCallback, never()).onSuccess(any());
        }
    }
}