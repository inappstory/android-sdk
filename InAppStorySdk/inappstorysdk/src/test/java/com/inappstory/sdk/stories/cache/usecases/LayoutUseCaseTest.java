package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.impl.IASProjectSettingsImpl;
import com.inappstory.sdk.core.api.impl.IASSettingsImpl;
import com.inappstory.sdk.core.network.content.models.LayoutResponse;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.game.cache.SimpleUseCaseError;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.ILruCachesHolder;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.network.ApiInterface;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.parse.to.ToLayoutResponse;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.StringsUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class LayoutUseCaseTest {

    @Mock
    private IASCore core;
    @Mock
    private ILruCachesHolder cachesHolder;
    @Mock
    private LruDiskCache infiniteCache;
    @Mock
    private UseCaseCallback<String> useCaseCallback;
    @Mock
    private IASSettingsImpl settingsHolder;
    @Mock
    private CachedSessionData sessionData;
    @Mock
    private NetworkClient networkClient;
    @Mock
    private ApiInterface apiInterface;
    @Mock
    private CacheJournalItem mockJournalItem;
    @Mock
    private DownloadFileState mockFileState;

    @TempDir
    File tempDir;

    private static final String MOCK_TIMESTAMP = "1234567890";
    private static final String MOCK_API_KEY = "test_api_key";
    private static final String MOCK_MD5 = "mocked_md5_hash";
    private static final String MOCK_LAYOUT_CONTENT = "{\"test\": \"layout\"}";

    private String generatedFilePath;
    private MockedStatic<StringsUtils> stringsUtilsMock;
    private MockedStatic<InAppStoryManager> inAppStoryManagerMock;
    private MockedConstruction<FilePathCacheGenerator> filePathGeneratorMock;
    private MockedConstruction<ConnectionCheck> connectionCheckMock;
    private MockedConstruction<FileOutputStream> fileOutputStreamMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Early initialization of the generated path before constructor is called
        generatedFilePath = new File(tempDir, "mock_layout.json").getAbsolutePath();

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

        // Intercept ConnectionCheck to immediately trigger the success callback
        connectionCheckMock = Mockito.mockConstruction(ConnectionCheck.class,
                new MockedConstruction.MockInitializer<ConnectionCheck>() {
                    @Override
                    public void prepare(ConnectionCheck mock, MockedConstruction.Context context) throws Throwable {
                        doAnswer(new Answer<Void>() {
                            @Override
                            public Void answer(InvocationOnMock invocation) throws Throwable {
                                ConnectionCheckCallback callback = invocation.getArgument(1);
                                callback.success();
                                return null;
                            }
                        }).when(mock).check(any(), any());
                    }
                }
        );


        when(core.projectSettingsAPI()).thenReturn(mock(IASProjectSettingsImpl.class));
        when(core.projectSettingsAPI().apiKey()).thenReturn(MOCK_API_KEY);
        when(core.cachesHolder()).thenReturn(cachesHolder);
        when(cachesHolder.getInfiniteCache()).thenReturn(infiniteCache);
        when(core.settingsAPI()).thenReturn(settingsHolder);
        when(settingsHolder.sessionData()).thenReturn(sessionData);
        when(core.network()).thenReturn(networkClient);
        when(networkClient.getApi()).thenReturn(apiInterface);

        sessionData.userId = "user_123";
        sessionData.sessionId = "session_456";
        sessionData.locale = "en";
    }

    @AfterEach
    void tearDown() {
        if (stringsUtilsMock != null) stringsUtilsMock.close();
        if (inAppStoryManagerMock != null) inAppStoryManagerMock.close();
        if (filePathGeneratorMock != null) filePathGeneratorMock.close();
        if (connectionCheckMock != null) connectionCheckMock.close();
        if (fileOutputStreamMock != null) fileOutputStreamMock.close();
    }

    private LayoutUseCase createUseCase() {
        return new LayoutUseCase(core, useCaseCallback, MOCK_TIMESTAMP);
    }

    @Test
    void testGetFile_UsesValidLocalFile() throws Exception {
        File realFile = new File(generatedFilePath);
        assertTrue(realFile.createNewFile());

        when(infiniteCache.getJournalItem(MOCK_MD5)).thenReturn(mockJournalItem);
        when(infiniteCache.get(MOCK_MD5)).thenReturn(mockFileState);
        when(mockFileState.getFullFile()).thenReturn(realFile);

        LayoutUseCase useCase = createUseCase();
        boolean result = useCase.getLocalFile();

        assertTrue(result);
        verify(useCaseCallback).onSuccess(""); // Empty file content
        verify(networkClient, never()).enqueue(any(), any(), any());
    }

    @Test
    void testGetFile_DownloadsWhenLocalFileMissing() {
        when(infiniteCache.getJournalItem(MOCK_MD5)).thenReturn(null);

        LayoutUseCase useCase = createUseCase();
        useCase.getFile();
        verify(apiInterface).getLayout(
                eq("user_123"),
                eq("session_456"),
                eq("en")
        );
    }

    @Test
    void testDownloadFile_FailsWhenSessionNotOpened() {
        when(infiniteCache.getJournalItem(MOCK_MD5)).thenReturn(null);
        when(settingsHolder.sessionData()).thenReturn(null);

        LayoutUseCase useCase = createUseCase();
        useCase.getFile();

        verify(useCaseCallback).onError(argThat(new ArgumentMatcher<SimpleUseCaseError>() {
            @Override
            public boolean matches(SimpleUseCaseError error) {
                return error != null && error.message().contains("Session is not opened");
            }
        }));
        verify(apiInterface, never()).getLayout(anyString(), anyString(), anyString());
    }

    @Test
    void testDownloadFile_HandlesNoInternetConnection() {
        // Reconfigure ConnectionCheck mock to simulate offline state for this specific test
        connectionCheckMock.close();
        connectionCheckMock = Mockito.mockConstruction(ConnectionCheck.class,
                new MockedConstruction.MockInitializer<ConnectionCheck>() {
                    @Override
                    public void prepare(ConnectionCheck mock, MockedConstruction.Context context) throws Throwable {
                        doAnswer(new Answer<Void>() {
                            @Override
                            public Void answer(InvocationOnMock invocation) throws Throwable {
                                ConnectionCheckCallback callback = invocation.getArgument(1);
                                callback.error();
                                return null;
                            }
                        }).when(mock).check(any(), any());
                    }
                });

        when(infiniteCache.getJournalItem(MOCK_MD5)).thenReturn(null);

        LayoutUseCase useCase = createUseCase();
        useCase.getFile();

        verify(useCaseCallback).onError(argThat(new ArgumentMatcher<SimpleUseCaseError>() {
            @Override
            public boolean matches(SimpleUseCaseError error) {
                return error != null && error.message().contains("No internet connection");
            }
        }));
        verify(apiInterface, never()).getLayout(anyString(), anyString(), anyString());
    }


    @Test
    void testDownloadFile_HandlesNetworkError() {
        when(infiniteCache.getJournalItem(MOCK_MD5)).thenReturn(null);

        LayoutUseCase useCase = createUseCase();
        useCase.getFile();

        ArgumentCaptor<NetworkCallback<LayoutResponse>> callbackCaptor = ArgumentCaptor.forClass(NetworkCallback.class);
        verify(networkClient).enqueue(any(), callbackCaptor.capture(), eq(ToLayoutResponse.class));

        callbackCaptor.getValue().errorDefault("Connection timeout");

        verify(useCaseCallback).onError(argThat(new ArgumentMatcher<SimpleUseCaseError>() {
            @Override
            public boolean matches(SimpleUseCaseError error) {
                return error != null && error.message().equals("Connection timeout");
            }
        }));
    }

    @Test
    void testDownloadFile_HandlesEmptyResponse() {
        when(infiniteCache.getJournalItem(MOCK_MD5)).thenReturn(null);

        LayoutUseCase useCase = createUseCase();
        useCase.getFile();

        ArgumentCaptor<NetworkCallback<LayoutResponse>> callbackCaptor = ArgumentCaptor.forClass(NetworkCallback.class);
        verify(networkClient).enqueue(any(), callbackCaptor.capture(), eq(ToLayoutResponse.class));

        LayoutResponse emptyResponse = new LayoutResponse();
        emptyResponse.layout = null;
        callbackCaptor.getValue().onSuccess(emptyResponse);

        verify(useCaseCallback).onError(argThat(new ArgumentMatcher<SimpleUseCaseError>() {
            @Override
            public boolean matches(SimpleUseCaseError error) {
                return error != null && error.message().contains("Empty layout response");
            }
        }));
    }

    @Test
    void testDownloadFile_SuccessfulWrite() throws Exception {
        when(infiniteCache.getJournalItem(MOCK_MD5)).thenReturn(null);

        LayoutUseCase useCase = createUseCase();
        useCase.getFile();

        ArgumentCaptor<NetworkCallback<LayoutResponse>> callbackCaptor = ArgumentCaptor.forClass(NetworkCallback.class);
        verify(networkClient).enqueue(any(), callbackCaptor.capture(), eq(ToLayoutResponse.class));

        LayoutResponse successResponse = new LayoutResponse();
        successResponse.layout = MOCK_LAYOUT_CONTENT;

        callbackCaptor.getValue().onSuccess(successResponse);

        verify(useCaseCallback).onSuccess(MOCK_LAYOUT_CONTENT);
        verify(infiniteCache).put(any());

        // Verify file was actually written
        File writtenFile = new File(generatedFilePath);
        assertTrue(writtenFile.exists());
    }

    @Test
    void testDownloadFile_WriteToFileFails() throws Exception {
        when(infiniteCache.getJournalItem(MOCK_MD5)).thenReturn(null);

        // Force FileOutputStream to throw IOException
        fileOutputStreamMock = Mockito.mockConstruction(FileOutputStream.class,
                new MockedConstruction.MockInitializer<FileOutputStream>() {
                    @Override
                    public void prepare(FileOutputStream mock, MockedConstruction.Context context) throws Throwable {
                        doThrow(new IOException("Disk full")).when(mock).write(any(byte[].class));
                    }
                });

        LayoutUseCase useCase = createUseCase();
        useCase.getFile();

        ArgumentCaptor<NetworkCallback<LayoutResponse>> callbackCaptor = ArgumentCaptor.forClass(NetworkCallback.class);
        verify(networkClient).enqueue(any(), callbackCaptor.capture(), eq(ToLayoutResponse.class));

        LayoutResponse successResponse = new LayoutResponse();
        successResponse.layout = MOCK_LAYOUT_CONTENT;

        callbackCaptor.getValue().onSuccess(successResponse);

        verify(useCaseCallback).onError(argThat(new ArgumentMatcher<SimpleUseCaseError>() {
            @Override
            public boolean matches(SimpleUseCaseError error) {
                return error != null && error.message().contains("Failed to save layout to disk");
            }
        }));
        verify(infiniteCache, never()).put(any());
    }

    @Test
    void testGetLocalFile_DeletesCacheOnMismatch() throws Exception {
        File wrongFile = new File(tempDir, "wrong_file.json");
        assertTrue(wrongFile.createNewFile());

        when(infiniteCache.getJournalItem(MOCK_MD5)).thenReturn(mockJournalItem);
        when(infiniteCache.get(MOCK_MD5)).thenReturn(mockFileState);
        when(mockFileState.getFullFile()).thenReturn(wrongFile); // Path does not match generatedFilePath

        LayoutUseCase useCase = createUseCase();
        boolean result = useCase.getLocalFile();

        assertFalse(result);
        verify(infiniteCache).delete(MOCK_MD5);
        verify(useCaseCallback, never()).onSuccess(anyString());
    }

    @Test
    void testDeleteCacheKey_HandlesExceptionGracefully() throws Exception {
        doThrow(new Exception("Cache error")).when(infiniteCache).delete(anyString());

        LayoutUseCase useCase = createUseCase();

        when(infiniteCache.getJournalItem(anyString())).thenReturn(mockJournalItem);
        when(infiniteCache.get(anyString())).thenReturn(null);

        useCase.getLocalFile();

        verify(infiniteCache).delete(anyString());

        inAppStoryManagerMock.verify(new MockedStatic.Verification() {
            @Override
            public void apply() throws Throwable {
                InAppStoryManager.showDLog(anyString(), anyString());
            }
        }, atLeastOnce());
    }
}