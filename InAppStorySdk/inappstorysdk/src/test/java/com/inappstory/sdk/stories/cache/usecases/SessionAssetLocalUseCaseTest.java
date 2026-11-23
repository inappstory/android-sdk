package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.game.cache.SimpleUseCaseError;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.ILruCachesHolder;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.StringsUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SessionAssetLocalUseCaseTest {

    @Mock private IASCore core;
    @Mock private ILruCachesHolder cachesHolder;
    @Mock private LruDiskCache bundleCache;
    @Mock private UseCaseCallback<File> useCaseCallback;
    @Mock private SessionAsset cacheAsset;
    @Mock private CacheJournalItem mockJournalItem;
    @Mock private DownloadFileState mockFileState;

    @TempDir File tempDir;

    private static final String MOCK_FILENAME = "test_asset.txt";
    private static final String MOCK_MD5 = "mocked_md5_hash";
    private static final String MOCK_SHA1 = "valid_sha1_hash";
    private static final String MOCK_TYPE = "asset_type";
    private static final String MOCK_REPLACE_KEY = "replace_key_123";
    private static final String MOCK_MIME_TYPE = "text/plain";

    private String generatedFilePath;
    private MockedStatic<StringsUtils> stringsUtilsMock;
    private MockedStatic<InAppStoryManager> inAppStoryManagerMock;
    private MockedConstruction<FilePathCacheGenerator> filePathGeneratorMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Early initialization of the generated path before constructor is called
        generatedFilePath = new File(tempDir, MOCK_FILENAME).getAbsolutePath();

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

        filePathGeneratorMock = Mockito.mockConstruction(FilePathCacheGenerator.class, 
                new MockedConstruction.MockInitializer<FilePathCacheGenerator>() {
            @Override
            public void prepare(FilePathCacheGenerator mock, MockedConstruction.Context context) throws Throwable {
                when(mock.generate()).thenReturn(generatedFilePath);
            }
        });

        when(core.cachesHolder()).thenReturn(cachesHolder);
        when(cachesHolder.getBundleCache()).thenReturn(bundleCache);

        // Direct assignment of public fields for SessionAsset
        cacheAsset.filename = MOCK_FILENAME;
        cacheAsset.sha1 = MOCK_SHA1;
        cacheAsset.type = MOCK_TYPE;
        cacheAsset.replaceKey = MOCK_REPLACE_KEY;
        cacheAsset.mimeType = MOCK_MIME_TYPE;
    }

    @AfterEach
    void tearDown() {
        if (stringsUtilsMock != null) stringsUtilsMock.close();
        if (inAppStoryManagerMock != null) inAppStoryManagerMock.close();
        if (filePathGeneratorMock != null) filePathGeneratorMock.close();
    }

    private SessionAssetLocalUseCase createUseCase() {
        return new SessionAssetLocalUseCase(core, useCaseCallback, cacheAsset);
    }

    @Test
    void testGetFile_SuccessfullyReturnsLocalFile() throws Exception {
        File realFile = new File(generatedFilePath);
        assertTrue(realFile.createNewFile());

        when(bundleCache.getJournalItem(MOCK_MD5)).thenReturn(mockJournalItem);
        when(mockJournalItem.getSha1()).thenReturn(MOCK_SHA1);
        when(bundleCache.get(MOCK_MD5)).thenReturn(mockFileState);
        when(mockFileState.getFullFile()).thenReturn(realFile);

        SessionAssetLocalUseCase useCase = createUseCase();
        useCase.getFile();

        verify(useCaseCallback).onSuccess(realFile);
        verify(bundleCache, never()).delete(anyString());
    }

    @Test
    void testGetFile_FailsOnSha1Mismatch() throws Exception {
        when(bundleCache.getJournalItem(MOCK_MD5)).thenReturn(mockJournalItem);
        when(mockJournalItem.getSha1()).thenReturn("wrong_sha1_hash");

        SessionAssetLocalUseCase useCase = createUseCase();
        useCase.getFile();

        verify(bundleCache).delete(MOCK_MD5);
        verify(useCaseCallback).onError(argThat(new ArgumentMatcher<SimpleUseCaseError>() {
            @Override
            public boolean matches(SimpleUseCaseError error) {
                return error != null && error.message().contains("No local file");
            }
        }));
    }

    @Test
    void testGetFile_FailsWhenFileStateIsNull() throws Exception {
        when(bundleCache.getJournalItem(MOCK_MD5)).thenReturn(mockJournalItem);
        when(mockJournalItem.getSha1()).thenReturn(MOCK_SHA1);
        when(bundleCache.get(MOCK_MD5)).thenReturn(null);

        SessionAssetLocalUseCase useCase = createUseCase();
        useCase.getFile();

        verify(bundleCache, never()).delete(anyString());
        verify(useCaseCallback).onError(argThat(new ArgumentMatcher<SimpleUseCaseError>() {
            @Override
            public boolean matches(SimpleUseCaseError error) {
                return error != null && error.message().contains("No local file");
            }
        }));
    }

    @Test
    void testGetFile_FailsAndDeletesCacheWhenFullFileIsNull() throws Exception {
        when(bundleCache.getJournalItem(MOCK_MD5)).thenReturn(mockJournalItem);
        when(mockJournalItem.getSha1()).thenReturn(MOCK_SHA1);
        when(bundleCache.get(MOCK_MD5)).thenReturn(mockFileState);
        when(mockFileState.getFullFile()).thenReturn(null);

        SessionAssetLocalUseCase useCase = createUseCase();
        useCase.getFile();

        verify(bundleCache).delete(MOCK_MD5);
        verify(useCaseCallback).onError(argThat(new ArgumentMatcher<SimpleUseCaseError>() {
            @Override
            public boolean matches(SimpleUseCaseError error) {
                return error != null && error.message().contains("No local file");
            }
        }));
    }

    @Test
    void testGetFile_FailsWhenNoCacheJournalExists() throws Exception{
        when(bundleCache.getJournalItem(MOCK_MD5)).thenReturn(null);

        SessionAssetLocalUseCase useCase = createUseCase();
        useCase.getFile();

        verify(bundleCache, never()).delete(anyString());
        verify(useCaseCallback).onError(argThat(new ArgumentMatcher<SimpleUseCaseError>() {
            @Override
            public boolean matches(SimpleUseCaseError error) {
                return error != null && error.message().contains("No local file");
            }
        }));
    }

    @Test
    void testDeleteCacheKey_HandlesExceptionGracefully() throws Exception {
        doThrow(new Exception("Cache deletion error")).when(bundleCache).delete(anyString());

        when(bundleCache.getJournalItem(MOCK_MD5)).thenReturn(mockJournalItem);
        when(mockJournalItem.getSha1()).thenReturn("wrong_sha1_hash"); // Triggers deleteCacheKey

        SessionAssetLocalUseCase useCase = createUseCase();
        useCase.getFile();

        verify(bundleCache).delete(anyString());
        
        inAppStoryManagerMock.verify(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                InAppStoryManager.showDLog(eq(LoggerTags.IAS_WARN_TAG), anyString());
            }
        }, atLeastOnce());

        verify(useCaseCallback).onError(argThat(new ArgumentMatcher<SimpleUseCaseError>() {
            @Override
            public boolean matches(SimpleUseCaseError error) {
                return error != null && error.message().contains("No local file");
            }
        }));
    }

    @Test
    void testJournalMethods_ReturnCorrectValues() {
        SessionAssetLocalUseCase useCase = createUseCase();

        assertEquals(MOCK_TYPE, useCase.journalItemType());
        assertEquals(MOCK_SHA1, useCase.journalItemSha1());
        assertEquals(MOCK_REPLACE_KEY, useCase.journalItemReplaceKey());
        assertEquals(MOCK_MIME_TYPE, useCase.journalItemMimeType());
        assertEquals(bundleCache, useCase.getCache());
    }
}