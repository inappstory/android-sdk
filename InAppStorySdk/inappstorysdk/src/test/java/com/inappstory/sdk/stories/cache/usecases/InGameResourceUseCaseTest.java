package com.inappstory.sdk.stories.cache.usecases;

import android.system.Os;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.SimpleUseCaseError;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.FileCheckerResult;
import com.inappstory.sdk.lrudiskcache.FileCheckerSuccess;
import com.inappstory.sdk.lrudiskcache.ILruCachesHolder;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

public class InGameResourceUseCaseTest {

    @Mock private IASCore core;
    @Mock private ILruCachesHolder cachesHolder;
    @Mock private LruDiskCache infiniteCache;
    @Mock private FileChecker fileChecker;
    @Mock private UseCaseCallback<Void> useCaseCallback;
    @Mock private FilesDownloader filesDownloader;
    @Mock private ExceptionManager exceptionManager;
    @Mock private WebResource mockResource;

    @TempDir File tempDir;

    private static final String TEST_URL = "https://example.com/resource.dat";
    private static final String TEST_KEY = "res_key_123";
    private static final String GAME_INSTANCE_ID = "game_1";
    private static final String MOCK_MD5 = "mocked_md5_hash";
    private static final String MOCK_SHA1 = "valid_sha1";
    private static final long MOCK_SIZE = 1024L;
    private static final String SYMLINK_DIR = "/mock/symlink/dir/";

    private String generatedFilePath;
    private MockedStatic<StringsUtils> stringsUtilsMock;
    private MockedStatic<InAppStoryManager> inAppStoryManagerMock;
    private MockedStatic<Os> osMock;
    private MockedConstruction<FilePathCacheGenerator> filePathGeneratorMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Early initialization of the generated path before constructor is called
        generatedFilePath = new File(tempDir, "mock_resource.dat").getAbsolutePath();

        stringsUtilsMock = Mockito.mockStatic(StringsUtils.class);
        stringsUtilsMock.when(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                StringsUtils.md5(anyString());
            }
        }).thenReturn(MOCK_MD5);

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

        osMock = Mockito.mockStatic(Os.class);
        osMock.when(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                Os.remove(anyString());
            }
        }).thenAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                return null;
            }
        });
        osMock.when(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                Os.symlink(anyString(), anyString());
            }
        }).thenAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                return null;
            }
        });

        filePathGeneratorMock = Mockito.mockConstruction(FilePathCacheGenerator.class, 
                new MockedConstruction.MockInitializer<FilePathCacheGenerator>() {
            @Override
            public void prepare(FilePathCacheGenerator mock, MockedConstruction.Context context) throws Throwable {
                when(mock.generate()).thenReturn(generatedFilePath);
            }
        });

        when(core.cachesHolder()).thenReturn(cachesHolder);
        when(cachesHolder.getInfiniteCache()).thenReturn(infiniteCache);
        when(core.filesDownloader()).thenReturn(filesDownloader);
        when(core.exceptionManager()).thenReturn(exceptionManager);

        // Direct assignment of public fields
        mockResource.url = TEST_URL;
        mockResource.key = TEST_KEY;
        mockResource.size = MOCK_SIZE;
        mockResource.sha1 = MOCK_SHA1;
    }

    @AfterEach
    void tearDown() {
        if (stringsUtilsMock != null) stringsUtilsMock.close();
        if (inAppStoryManagerMock != null) inAppStoryManagerMock.close();
        if (osMock != null) osMock.close();
        if (filePathGeneratorMock != null) filePathGeneratorMock.close();
    }

    private InGameResourceUseCase createUseCase() {
        return new InGameResourceUseCase(
                core, fileChecker, SYMLINK_DIR, GAME_INSTANCE_ID, useCaseCallback, mockResource
        );
    }

    @Test
    void testGetFile_UsesValidCachedResource() throws Exception {
        File cachedFile = new File(tempDir, "cached.dat");
        assertTrue(cachedFile.createNewFile());
        when(infiniteCache.getFullFile(MOCK_MD5)).thenReturn(cachedFile);
        
        FileCheckerSuccess successResult = mock(FileCheckerSuccess.class);
        when(fileChecker.checkWithShaAndSize(eq(cachedFile), eq(MOCK_SIZE), eq(MOCK_SHA1), eq(true)))
                .thenReturn(successResult);

        InGameResourceUseCase useCase = createUseCase();
        useCase.getFile();

        verify(useCaseCallback).onSuccess(null);
        verify(filesDownloader, never()).downloadFile(any(), any(), any(), any(), any(), anyLong(), any());
    }

    @Test
    void testGetFile_UsesValidDiskResourceAndUpdatesCache() throws Exception {
        when(infiniteCache.getFullFile(MOCK_MD5)).thenReturn(null);

        File diskFile = new File(generatedFilePath);
        assertTrue(diskFile.createNewFile());
        
        FileCheckerSuccess successResult = mock(FileCheckerSuccess.class);
        when(fileChecker.checkWithShaAndSize(eq(diskFile), eq(MOCK_SIZE), eq(MOCK_SHA1), eq(true)))
                .thenReturn(successResult);

        InGameResourceUseCase useCase = createUseCase();
        useCase.getFile();

        verify(infiniteCache).put(any());
        verify(useCaseCallback).onSuccess(null);
        verify(filesDownloader, never()).downloadFile(any(), any(), any(), any(), any(), anyLong(), any());
    }

    @Test
    void testGetFile_ValidationFailsOnEmptyUrl() {
        mockResource.url = "";
        
        InGameResourceUseCase useCase = createUseCase();
        useCase.getFile();

        verify(useCaseCallback).onError(argThat(error -> 
                error instanceof SimpleUseCaseError && 
                error.message().contains("Wrong resource key or url")
        ));
        verify(filesDownloader, never()).downloadFile(any(), any(), any(), any(), any(), anyLong(), any());
    }

    @Test
    void testGetFile_InitiatesDownloadOnCacheMiss() throws Exception {
        when(infiniteCache.getFullFile(MOCK_MD5)).thenReturn(null);

        InGameResourceUseCase useCase = createUseCase();
        useCase.getFile();

        ArgumentCaptor<FileLoadProgressCallback> progressCaptor = ArgumentCaptor.forClass(FileLoadProgressCallback.class);
        ArgumentCaptor<FinishDownloadFileCallback> finishCaptor = ArgumentCaptor.forClass(FinishDownloadFileCallback.class);

        verify(filesDownloader).downloadFile(
                eq(TEST_URL),
                any(File.class),
                progressCaptor.capture(),
                any(),
                isNull(),
                eq(0L),
                finishCaptor.capture()
        );
    }

    @Test
    void testGetFile_HandlesDownloadInitiationException() {
        when(infiniteCache.getFullFile(MOCK_MD5)).thenReturn(null);
        doThrow(new RuntimeException("Network init failed")).when(filesDownloader).downloadFile(
                anyString(), any(File.class), any(), any(), isNull(), anyLong(), any()
        );

        InGameResourceUseCase useCase = createUseCase();
        useCase.getFile();

        verify(exceptionManager).createExceptionLog(any(RuntimeException.class));
        inAppStoryManagerMock.verify(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                InAppStoryManager.showDLog(eq(LoggerTags.IAS_WARN_TAG), anyString());
            }
        }, times(1));
        verify(useCaseCallback).onError(argThat(error -> 
                error instanceof SimpleUseCaseError && 
                error.message().contains("Failed to start download")
        ));
    }

    @Test
    void testFinishCallback_HandlesSuccessfulDownload() throws Exception {
        when(infiniteCache.getFullFile(MOCK_MD5)).thenReturn(null);

        InGameResourceUseCase useCase = createUseCase();
        useCase.getFile();

        ArgumentCaptor<FinishDownloadFileCallback> finishCaptor = ArgumentCaptor.forClass(FinishDownloadFileCallback.class);
        verify(filesDownloader).downloadFile(any(), any(), any(), any(), isNull(), anyLong(), finishCaptor.capture());

        File downloadedFile = new File(tempDir, "downloaded.dat");
        assertTrue(downloadedFile.createNewFile());
        
        DownloadFileState state = new DownloadFileState(downloadedFile, MOCK_SIZE, MOCK_SIZE);
        FileCheckerSuccess successResult = mock(FileCheckerSuccess.class);
        when(fileChecker.checkWithShaAndSize(eq(downloadedFile), eq(MOCK_SIZE), eq(MOCK_SHA1), eq(true)))
                .thenReturn(successResult);

        finishCaptor.getValue().finish(state);

        verify(infiniteCache).put(any());
        verify(useCaseCallback).onSuccess(null);
    }

    @Test
    void testFinishCallback_HandlesInterruptedDownload() throws Exception {
        when(infiniteCache.getFullFile(MOCK_MD5)).thenReturn(null);

        InGameResourceUseCase useCase = createUseCase();
        useCase.getFile();

        ArgumentCaptor<FinishDownloadFileCallback> finishCaptor = ArgumentCaptor.forClass(FinishDownloadFileCallback.class);
        verify(filesDownloader).downloadFile(any(), any(), any(), any(), isNull(), anyLong(), finishCaptor.capture());

        finishCaptor.getValue().finish(null);

        verify(useCaseCallback).onError(argThat(error -> 
                error instanceof SimpleUseCaseError && 
                error.message().contains("Download interrupted")
        ));
        verify(infiniteCache, never()).put(any());
    }

    @Test
    void testFinishCallback_HandlesIntegrityCheckFailure() throws Exception {
        when(infiniteCache.getFullFile(MOCK_MD5)).thenReturn(null);

        InGameResourceUseCase useCase = createUseCase();
        useCase.getFile();

        ArgumentCaptor<FinishDownloadFileCallback> finishCaptor = ArgumentCaptor.forClass(FinishDownloadFileCallback.class);
        verify(filesDownloader).downloadFile(any(), any(), any(), any(), isNull(), anyLong(), finishCaptor.capture());

        File downloadedFile = new File(tempDir, "corrupted.dat");
        assertTrue(downloadedFile.createNewFile());
        
        DownloadFileState state = new DownloadFileState(downloadedFile, MOCK_SIZE, MOCK_SIZE);
        FileCheckerResult errorResult = mock(FileCheckerResult.class);
        when(errorResult.toString()).thenReturn("SHA mismatch");
        
        when(fileChecker.checkWithShaAndSize(eq(downloadedFile), eq(MOCK_SIZE), eq(MOCK_SHA1), eq(true)))
                .thenReturn(errorResult);

        finishCaptor.getValue().finish(state);

        verify(useCaseCallback).onError(argThat(error -> 
                error instanceof SimpleUseCaseError && 
                error.message().contains("Wrong size or sha1")
        ));
        verify(infiniteCache, never()).put(any());
    }

    @Test
    void testFinishCallback_HandlesCacheJournalUpdateException() throws Exception {
        when(infiniteCache.getFullFile(MOCK_MD5)).thenReturn(null);
        doThrow(new RuntimeException("Disk full")).when(infiniteCache).put(any());

        InGameResourceUseCase useCase = createUseCase();
        useCase.getFile();

        ArgumentCaptor<FinishDownloadFileCallback> finishCaptor = ArgumentCaptor.forClass(FinishDownloadFileCallback.class);
        verify(filesDownloader).downloadFile(any(), any(), any(), any(), isNull(), anyLong(), finishCaptor.capture());

        File downloadedFile = new File(tempDir, "downloaded.dat");
        assertTrue(downloadedFile.createNewFile());

        DownloadFileState state = new DownloadFileState(downloadedFile, MOCK_SIZE, MOCK_SIZE);
        FileCheckerSuccess successResult = mock(FileCheckerSuccess.class);
        when(fileChecker.checkWithShaAndSize(eq(downloadedFile), eq(MOCK_SIZE), eq(MOCK_SHA1), eq(true)))
                .thenReturn(successResult);

        finishCaptor.getValue().finish(state);

        inAppStoryManagerMock.verify(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                InAppStoryManager.showDLog(eq(LoggerTags.IAS_WARN_TAG), anyString());
            }
        }, times(1));

        verify(useCaseCallback).onError(argThat(error ->
                error instanceof SimpleUseCaseError &&
                        error.message().contains("Failed to save file metadata")
        ));
    }

    @Test
    void testCreateSymlinks_HandlesOsSymlinkFailureGracefully() throws Exception {
        osMock.when(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                Os.symlink(anyString(), anyString());
            }
        }).thenThrow(new RuntimeException("Simulated OS symlink error"));

        when(infiniteCache.getFullFile(MOCK_MD5)).thenReturn(null);

        InGameResourceUseCase useCase = createUseCase();
        useCase.getFile();

        inAppStoryManagerMock.verify(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                InAppStoryManager.showDLog(eq(LoggerTags.IAS_WARN_TAG), anyString());
            }
        }, atLeastOnce());
        
        verify(filesDownloader).downloadFile(any(), any(), any(), any(), isNull(), anyLong(), any());
    }
}