package com.inappstory.sdk.lrudiskcache;

import android.content.Context;
import android.content.res.Resources;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.lang.reflect.Field;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_1;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_50;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class LruCachesHolderTest {

    @Mock private IASCore core;
    @Mock private Context context;
    @Mock private Context appContext;
    @Mock private Resources resources;
    @Mock private File filesDir;

    @Mock private LruDiskCache mockFastCache;
    @Mock private LruDiskCache mockCommonCache;
    @Mock private LruDiskCache mockInfiniteCache;
    @Mock private LruDiskCache mockBundleCache;
    @Mock private LruDiskCache mockVodCache;
    @Mock private VODCacheJournal mockVodJournal;

    private LruCachesHolder holder;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(core.appContext()).thenReturn(appContext);
        when(appContext.getApplicationContext()).thenReturn(context);
        when(context.getFilesDir()).thenReturn(filesDir);
        when(filesDir.getFreeSpace()).thenReturn(1024L * 1024L * 1024L);

        when(context.getResources()).thenReturn(resources);
        when(resources.getString(anyInt())).thenReturn("Min free space error");
        when(context.getString(anyInt())).thenReturn("Min free space error");

        holder = new LruCachesHolder(core, CacheSize.MEDIUM);

        // Wait for the background initialization thread to complete
        // to prevent it from overwriting our mocks with real instances
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        setPrivateField(holder, "fastCache", mockFastCache);
        setPrivateField(holder, "commonCache", mockCommonCache);
        setPrivateField(holder, "infiniteCache", mockInfiniteCache);
        setPrivateField(holder, "bundleCache", mockBundleCache);
        setPrivateField(holder, "vodCache", mockVodCache);
        setPrivateField(holder, "vodCacheJournal", mockVodJournal);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = LruCachesHolder.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private Object getPrivateField(Object target, String fieldName) throws Exception {
        Field field = LruCachesHolder.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    @Test
    void testGetCacheOrReinit_TriggerReinitForDummy() throws Exception {
        LruDiskCache actualDummyCache = (LruDiskCache) getPrivateField(holder, "dummyCache");
        setPrivateField(holder, "fastCache", actualDummyCache);

        LruCachesHolder spyHolder = Mockito.spy(holder);
        doNothing().when(spyHolder).scheduleReinit();

        spyHolder.getFastCache();

        verify(spyHolder, times(1)).scheduleReinit();
    }

    @Test
    void testSetCacheSizes_SufficientSpace() {
        long plentyOfSpace = 1024L * 1024L * 1024L;

        holder.setCacheSizes(plentyOfSpace);

        verify(mockFastCache).setCacheSize(MB_10);
        verify(mockCommonCache).setCacheSize(MB_50);
    }

    @Test
    void testSetCacheSizes_CriticalLowSpace() throws Exception {
        LruCachesHolder smallHolder = new LruCachesHolder(core, CacheSize.SMALL);

        // Wait for the background thread of smallHolder to finish initialization
        // so it doesn't overwrite our mocks with real instances
        Thread.sleep(100);

        setPrivateField(smallHolder, "fastCache", mockFastCache);
        setPrivateField(smallHolder, "commonCache", mockCommonCache);

        long criticalSpace = 5L * 1024L * 1024L;

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

            smallHolder.setCacheSizes(criticalSpace);

            verify(mockFastCache).setCacheSize(MB_1);
            verify(mockCommonCache).setCacheSize(MB_1);

            mockedManager.verify(new MockedStatic.Verification() {
                @Override
                public void apply() throws Throwable {
                    InAppStoryManager.showELog(eq(LoggerTags.IAS_ERROR_TAG), anyString());
                }
            }, times(1));
        }
    }

    @Test
    void testSetCacheSizes_IgnoresIfDummy() {
        LruCachesHolder freshHolder = new LruCachesHolder(core, CacheSize.LARGE);

        assertDoesNotThrow(new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                freshHolder.setCacheSizes(1024L * 1024L * 1024L);
            }
        });
    }

    @Test
    void testClear_ClearsAllExceptBundle() throws Exception {
        holder.clear();

        verify(mockFastCache, times(1)).clearCache();
        verify(mockCommonCache, times(1)).clearCache();
        verify(mockInfiniteCache, times(1)).clearCache();
        verify(mockVodCache, times(1)).clearCache();
        verify(mockVodJournal, times(1)).clear();

        verify(mockBundleCache, never()).clearCache();
    }

    @Test
    void testCheckFreeSpace_RunnableDelegatesToSetCacheSizes() throws Exception {
        long freeSpace = 500L * 1024L * 1024L;

        doReturn(freeSpace).when(filesDir).getFreeSpace();
        doReturn(filesDir).when(mockCommonCache).getCacheDir();

        // Ensure mocks are actively in place right before execution
        setPrivateField(holder, "fastCache", mockFastCache);
        setPrivateField(holder, "commonCache", mockCommonCache);

        holder.checkFreeSpace.run();

        verify(mockFastCache).setCacheSize(MB_10);
        verify(mockCommonCache).setCacheSize(MB_50);
    }
}