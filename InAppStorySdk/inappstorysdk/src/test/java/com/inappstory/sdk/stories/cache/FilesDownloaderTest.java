package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;
import com.inappstory.sdk.stories.cache.usecases.FinishDownloadFileCallback;
import com.inappstory.sdk.stories.cache.vod.BytesForRange;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FilesDownloaderTest {

    @Mock private IASCore core;
    @Mock private NetworkClient networkClient;
    @Mock private KeyValueStorage keyValueStorage;

    @TempDir File tempDir;

    private FilesDownloader downloader;
    private FilesDownloader spyDownloader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(core.network()).thenReturn(networkClient);
        when(networkClient.userAgent()).thenReturn("TestAgent/1.0");
        when(core.keyValueStorage()).thenReturn(keyValueStorage);


        downloader = new FilesDownloader(core, 1000, 1000);
        spyDownloader = Mockito.spy(downloader);

        doReturn(false).when(spyDownloader).addFinishCallback(
                anyString(),
                anyString(),
                any(FinishDownloadFileCallback.class)
        );
    }

    @Test
    void testSuccessfulFreshDownload() throws Exception {
        byte[] fileContent = "Hello, InAppStory!".getBytes();

        HttpURLConnection mockConn = mock(HttpURLConnection.class);
        when(mockConn.getResponseCode()).thenReturn(200);
        when(mockConn.getContentLength()).thenReturn(fileContent.length);
        when(mockConn.getHeaderField("Content-Type")).thenReturn("image/jpeg");
        when(mockConn.getHeaderField("Content-Encoding")).thenReturn(null);
        when(mockConn.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("image/jpeg"));
        when(mockConn.getHeaderFields()).thenReturn(headers);

        doReturn(mockConn).when(spyDownloader).createConnection(anyString(), anyLong(), anyLong(), anyBoolean());

        File outputFile = new File(tempDir, "test.file");
        String url = "http://localhost/test.file";
        ApiLogResponse apiLogResponse = new ApiLogResponse();

        DownloadFileState result = spyDownloader.downloadFile(
                url, outputFile, null, apiLogResponse, null, 0,
                new FinishDownloadFileCallback() {
                    @Override
                    public void finish(DownloadFileState state) {
                    }
                    @Override
                    public void waiting() {
                    }
                }
        );

        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isFullDownload());
        assertEquals(fileContent.length, result.totalSize);
        assertTrue(outputFile.exists());
        assertArrayEquals(fileContent, Files.readAllBytes(outputFile.toPath()));

        verify(keyValueStorage).saveString("test.file", "image/jpeg");
    }

    @Test
    void testResumableDownload() throws Exception {
        byte[] part1 = "Hello, ".getBytes();
        byte[] part2 = "InAppStory!".getBytes();
        long totalSize = part1.length + part2.length;

        File outputFile = new File(tempDir, "resume.file");
        Files.write(outputFile.toPath(), part1);

        HttpURLConnection mockConn = mock(HttpURLConnection.class);
        when(mockConn.getResponseCode()).thenReturn(206);
        when(mockConn.getContentLength()).thenReturn(part2.length);
        when(mockConn.getHeaderField("Content-Range")).thenReturn("bytes 7-18/" + totalSize);
        when(mockConn.getHeaderField("Content-Type")).thenReturn("text/plain");
        when(mockConn.getHeaderField("Content-Encoding")).thenReturn(null);
        when(mockConn.getInputStream()).thenReturn(new ByteArrayInputStream(part2));

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Range", Collections.singletonList("bytes 7-18/" + totalSize));
        headers.put("Content-Type", Collections.singletonList("text/plain"));
        when(mockConn.getHeaderFields()).thenReturn(headers);

        doReturn(mockConn).when(spyDownloader).createConnection(anyString(), anyLong(), anyLong(), anyBoolean());

        String url = "http://localhost/resume.file";
        ApiLogResponse apiLogResponse = new ApiLogResponse();

        DownloadFileState result = spyDownloader.downloadFile(
                url, outputFile, null, apiLogResponse, null, 7,
                new FinishDownloadFileCallback() {
                    @Override
                    public void finish(DownloadFileState state) {
                    }
                    @Override
                    public void waiting() {
                    }
                }
        );

        assertNotNull(result);
        assertTrue(result.isFullDownload());
        assertEquals(totalSize, result.totalSize);

        byte[] finalContent = Files.readAllBytes(outputFile.toPath());
        assertArrayEquals("Hello, InAppStory!".getBytes(), finalContent, "Файл должен быть корректно склеен");
    }

    @Test
    void testDownloadFailsOnConnectionError() throws Exception {
        doThrow(new java.io.IOException("Connection refused"))
                .when(spyDownloader).createConnection(anyString(), anyLong(), anyLong(), anyBoolean());

        File outputFile = new File(tempDir, "error.file");
        String url = "http://localhost/error.file";

        DownloadFileState result = spyDownloader.downloadFile(
                url, outputFile, null, new ApiLogResponse(), null, 0,
                new FinishDownloadFileCallback() {
                    @Override
                    public void finish(DownloadFileState state) {
                    }
                    @Override
                    public void waiting() {
                    }
                }
        );

        assertNull(result, "При ошибке соединения результат должен быть null");
        assertFalse(outputFile.exists(), "Файл не должен быть создан при ошибке");
    }

    @Test
    void testDownloadInterruption() throws Exception {
        byte[] largeData = new byte[100000];

        HttpURLConnection mockConn = mock(HttpURLConnection.class);
        when(mockConn.getResponseCode()).thenReturn(200);
        when(mockConn.getContentLength()).thenReturn(largeData.length);
        when(mockConn.getHeaderField("Content-Type")).thenReturn("application/octet-stream");
        when(mockConn.getHeaderField("Content-Encoding")).thenReturn(null);

        when(mockConn.getInputStream()).thenReturn(new InputStream() {
            private int pos = 0;

            @Override
            public int read() throws IOException {
                return 0;
            }

            @Override
            public int read(byte[] b, int off, int len) {
                if (pos >= largeData.length) return -1;
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                int toRead = Math.min(len, largeData.length - pos);
                System.arraycopy(largeData, pos, b, off, toRead);
                pos += toRead;
                return toRead;
            }
        });

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/octet-stream"));
        when(mockConn.getHeaderFields()).thenReturn(headers);

        doReturn(mockConn).when(spyDownloader).createConnection(anyString(), anyLong(), anyLong(), anyBoolean());

        File outputFile = new File(tempDir, "interrupted.file");
        final DownloadInterruption interruption = new DownloadInterruption();
        interruption.active = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                    interruption.active = true;
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        String url = "http://localhost/large.file";

        DownloadFileState result = spyDownloader.downloadFile(
                url, outputFile, null, new ApiLogResponse(), interruption, 0,
                new FinishDownloadFileCallback() {
                    @Override
                    public void finish(DownloadFileState state) {
                    }
                    @Override
                    public void waiting() {
                    }
                }
        );

        assertNotNull(result);
        assertFalse(result.isFullDownload());
        assertTrue(outputFile.length() > 0);
        assertTrue(outputFile.length() < largeData.length);
    }

    @Test
    void testDownloadBytesSuccess() throws Exception {
        byte[] rangeData = "RANGE_DATA".getBytes();

        HttpURLConnection mockConn = mock(HttpURLConnection.class);
        when(mockConn.getResponseCode()).thenReturn(206);
        when(mockConn.getContentLength()).thenReturn(rangeData.length);
        when(mockConn.getHeaderField("Content-Range")).thenReturn("bytes 10-19/100");
        when(mockConn.getHeaderField("Content-Encoding")).thenReturn(null);
        when(mockConn.getInputStream()).thenReturn(new ByteArrayInputStream(rangeData));

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Range", Collections.singletonList("bytes 10-19/100"));
        when(mockConn.getHeaderFields()).thenReturn(headers);

        doReturn(mockConn).when(spyDownloader).createConnection(anyString(), anyLong(), anyLong(), anyBoolean());

        String url = "http://localhost/bytes.file";

        BytesForRange result = spyDownloader.downloadBytes(url, 10, 19, 1024 * 1024);

        assertNotNull(result);
        assertArrayEquals(rangeData, result.content());
        assertEquals(10, result.range().start());
        assertEquals(19, result.range().end());
        assertEquals(100, result.range().length());
    }


    @Test
    void testDownloadFileWithNullOutputFile() throws Exception {

        DownloadFileState result = spyDownloader.downloadFile(
                "http://localhost/test.file",
                null,
                null,
                new ApiLogResponse(),
                null,
                0,
                new FinishDownloadFileCallback() {
                    @Override
                    public void finish(DownloadFileState state) {
                    }
                    @Override
                    public void waiting() {
                    }
                }
        );

        assertNull(result, "При null outputFile метод должен вернуть null");
    }


    @Test
    void testDownloadFailsOnHttp404() throws Exception {
        HttpURLConnection mockConn = mock(HttpURLConnection.class);
        when(mockConn.getResponseCode()).thenReturn(404);
        when(mockConn.getHeaderField("Content-Encoding")).thenReturn(null);
        when(mockConn.getErrorStream()).thenReturn(new ByteArrayInputStream("Not Found".getBytes()));

        Map<String, List<String>> headers = new HashMap<>();
        when(mockConn.getHeaderFields()).thenReturn(headers);

        doReturn(mockConn).when(spyDownloader).createConnection(anyString(), anyLong(), anyLong(), anyBoolean());

        File outputFile = new File(tempDir, "error404.file");

        DownloadFileState result = spyDownloader.downloadFile(
                "http://localhost/error.file", outputFile, null, new ApiLogResponse(), null, 0,
                new FinishDownloadFileCallback() {
                    @Override
                    public void finish(DownloadFileState state) {
                    }
                    @Override
                    public void waiting() {
                    }
                }
        );

        assertNull(result, "При HTTP 404 результат должен быть null");
        assertFalse(outputFile.exists(), "Файл не должен быть создан при ошибке HTTP");
    }

    @Test
    void testDownloadBytesFailsOnInsufficientSpace() throws Exception {
        HttpURLConnection mockConn = mock(HttpURLConnection.class);
        when(mockConn.getResponseCode()).thenReturn(200);
        when(mockConn.getContentLength()).thenReturn(1000);
        when(mockConn.getHeaderFields()).thenReturn(new HashMap<String, List<String>>());

        doReturn(mockConn).when(spyDownloader).createConnection(anyString(), anyLong(), anyLong(), anyBoolean());


        BytesForRange result = spyDownloader.downloadBytes("http://localhost/bytes.file", 0, 10, 100);

        assertNull(result, "При нехватке места downloadBytes должен вернуть null");
    }


    @Test
    void testDownloadBytesFailsOnHttp500() throws Exception {
        HttpURLConnection mockConn = mock(HttpURLConnection.class);
        when(mockConn.getResponseCode()).thenReturn(500);
        when(mockConn.getHeaderFields()).thenReturn(new HashMap<String, List<String>>());

        doReturn(mockConn).when(spyDownloader).createConnection(anyString(), anyLong(), anyLong(), anyBoolean());

        BytesForRange result = spyDownloader.downloadBytes("http://localhost/bytes.file", 0, 10, 1024 * 1024);

        assertNull(result, "При HTTP 500 downloadBytes должен вернуть null");
    }


    @Test
    void testDownloadBytesFailsOnConnectionError() throws Exception {

        doThrow(new java.io.IOException("Connection refused"))
                .when(spyDownloader).createConnection(anyString(), anyLong(), anyLong(), anyBoolean());

        BytesForRange result = spyDownloader.downloadBytes("http://localhost/bytes.file", 0, 10, 1024 * 1024);

        assertNull(result, "При ошибке соединения downloadBytes должен вернуть null");
    }
}