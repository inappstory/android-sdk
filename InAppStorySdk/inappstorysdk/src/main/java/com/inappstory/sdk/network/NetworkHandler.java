package com.inappstory.sdk.network;

import static java.util.UUID.randomUUID;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequestHeader;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;

import org.brotli.dec.BrotliInputStream;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public final class NetworkHandler implements InvocationHandler {
    /**
     * Трекер аналитики
     */

    public NetworkHandler() {
    }

    static URL getURL(Request req) throws Exception {
        String url = req.getUrl();
        StringBuilder varStr = new StringBuilder();
        if (!(req.getVarList().isEmpty() && req.getVars().isEmpty())) {
            for (Object key : req.getVarKeys()) {
                varStr.append("&").append(key).append("=").append(req.getVars().get(key));
            }
            for (Object keyVal : req.getVarList()) {
                Pair<String, String> locVal = (Pair<String, String>) keyVal;
                varStr.append("&").append(locVal.first).append("=").append(locVal.second);
            }
            varStr = new StringBuilder("?" + varStr.substring(1));
        }
        return new URL(url + varStr);
    }

    public static boolean sessionKill = false;

    public static Response doRequest(Request req, String requestId)
            throws Exception {
        ApiLogRequest requestLog = new ApiLogRequest();
        URL url = getURL(req);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod(req.getMethod());
        requestLog.method = req.getMethod();
        requestLog.url = url.toString();
        requestLog.timestamp = System.currentTimeMillis();
        requestLog.id = requestId;

        if (req.getHeaders() != null) {
            for (Object key : req.getHeaders().keySet()) {
                // requestLog.headers.add(new ApiLogRequestHeader(key.toString(), req.getHeader(key)));
                connection.setRequestProperty(key.toString(), req.getHeader(key));
            }
        }
        if (InAppStoryService.getInstance() != null && InAppStoryService.getInstance().getUserId() != null) {
            connection.setRequestProperty("X-User-id", InAppStoryService.getInstance().getUserId());

        }
        connection.setRequestProperty("Accept-Encoding", "br, gzip");
        connection.setRequestProperty("X-Request-ID", randomUUID().toString());
        if (req.isFormEncoded()) {
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        }
        if (!sessionKill && !Session.needToUpdate() && !req.getUrl().contains("session/open")) {
            connection.setRequestProperty("auth-session-id", Session.getInstance().id);
        }


        InAppStoryManager.showDLog("InAppStory_Network", requestId + " " + connection.getRequestProperties().toString());

        for (Map.Entry<String, List<String>> entry : connection.getRequestProperties().entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && !entry.getValue().isEmpty())
                requestLog.headers.add(new ApiLogRequestHeader(entry.getKey(), entry.getValue().get(0)));
        }
        if (!req.getMethod().equals(GET) && !req.getMethod().equals(HEAD) && !req.getBody().isEmpty()) {
            InAppStoryManager.showDLog("InAppStory_Network", requestId + " " + req.getBody());
            requestLog.body = req.getBody();
            requestLog.bodyRaw = req.getBodyRaw();
            requestLog.bodyUrlEncoded = req.getBodyEncoded();
            if (!req.isFormEncoded()) {
                connection.setRequestProperty("Content-Type", "application/json");
                requestLog.headers.add(
                        new ApiLogRequestHeader("Content-Type", "application/json"));
            }
            InAppStoryManager.sendApiRequestLog(requestLog);

            connection.setDoOutput(true);
            OutputStream outStream = connection.getOutputStream();
            OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
            outStreamWriter.write(req.getBody());
            outStreamWriter.flush();
            outStreamWriter.close();
            outStream.close();
        } else {
            InAppStoryManager.sendApiRequestLog(requestLog);
        }

        int statusCode = connection.getResponseCode();
        Response respObject = null;
        InAppStoryManager.showDLog("InAppStory_Network", requestId + " " + connection.getURL().toString() + " \nStatus Code: " + statusCode);
        //apiLog.duration = System.currentTimeMillis() - start;
        long contentLength = 0;
        String decompression = null;
        HashMap<String, String> responseHeaders = getHeaders(connection);
        if (responseHeaders.containsKey("Content-Encoding")) {
            decompression = responseHeaders.get("Content-Encoding");
        }
        if (responseHeaders.containsKey("content-encoding")) {
            decompression = responseHeaders.get("content-encoding");
        }
        if (statusCode == 200 || statusCode == 201 || statusCode == 202) {
            String res = getResponseFromStream(connection.getInputStream(), decompression);
            contentLength = res.length();
            InAppStoryManager.showDLog("InAppStory_Network", requestId + " Response: " + res);

            respObject = new Response.Builder().contentLength(contentLength).
                    headers(responseHeaders).code(statusCode).body(res).build();
        } else {
            String res = getResponseFromStream(connection.getErrorStream(), decompression);
            contentLength = res.length();
            InAppStoryManager.showDLog("InAppStory_Network", requestId + " Error: " + res);
            respObject = new Response.Builder().contentLength(contentLength).
                    headers(responseHeaders).code(statusCode).errorBody(res).build();
        }
        ApiLogResponse responseLog = new ApiLogResponse();
        responseLog.id = requestId;
        responseLog.timestamp = System.currentTimeMillis();
        responseLog.contentLength = respObject.contentLength;
        if (respObject.body != null) {
            responseLog.generateJsonResponse(respObject.code, respObject.body, respObject.headers);
        } else {
            responseLog.generateError(respObject.code, respObject.errorBody, respObject.headers);
        }
        InAppStoryManager.sendApiResponseLog(responseLog);
        connection.disconnect();
        return respObject;
    }

    //Test
    public static String getResponseFromStream(InputStream inputStream, String decompression) throws Exception {
        BufferedReader bufferedReader;
        if (decompression != null) {
            switch (decompression) {
                case "br":
                    bufferedReader = new BufferedReader(new InputStreamReader(new BrotliInputStream(inputStream)));
                    break;
                case "gzip":
                    bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(inputStream)));
                    break;
                default:
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            }
        } else {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        }

        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        return response.toString();
    }

    public static HashMap<String, String> getHeaders(@NonNull final HttpURLConnection connection) {
        final HashMap<String, String> headers = new HashMap<>();
        for (final Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            final String key = header.getKey();
            if (key != null) {
                headers.put(key, header.getValue().get(0));
            }
        }
        return headers;
    }

    @Override
    public Request invoke(Object proxy, Method method, Object[] args) {
        if (networkClient == null) networkClient = NetworkClient.getInstance();
        GET get = method.getAnnotation(GET.class);
        POST post = method.getAnnotation(POST.class);
        DELETE delete = method.getAnnotation(DELETE.class);
        PUT put = method.getAnnotation(PUT.class);
        if (delete != null) {
            return generateRequest(delete.value(), method.getParameterAnnotations(), args, (new Request.Builder()).delete());
        } else if (get != null) {
            return generateRequest(get.value(), method.getParameterAnnotations(), args, (new Request.Builder()).get());
        } else {
            boolean encoded = (method.getAnnotation(FormUrlEncoded.class) != null);
            if (post != null) {
                return generateRequest(post.value(), method.getParameterAnnotations(), args, (new Request.Builder()).post().isFormEncoded(encoded));
            } else if (put != null) {
                return generateRequest(put.value(), method.getParameterAnnotations(), args, (new Request.Builder()).put().isFormEncoded(encoded));
            } else {
                throw new IllegalStateException("Don't know what to do.");
            }
        }
    }

    /**
     * Отправка событий. Берем параметры события из массива args
     *
     * @param ev     событие
     * @param method метод из которого взяли событие для получения метаданных
     * @param args   параметры события
     */
    HashMap<String, String> headers;

    //Test
    public String encode(String var) {
        try {
            return URLEncoder.encode(var, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
        }
        return var;
    }


    //Test
    public Request generateRequest(String path, Annotation[][] parameterAnnotations, Object[] args, Request.Builder builder) {
        if (networkClient == null) networkClient = NetworkClient.getInstance();
        //
        HashMap<String, String> vars = new HashMap<>();
        ArrayList<Pair<String, String>> varList = new ArrayList<>();
        // String path = ev.value();
        String bodyRaw = "";
        String bodyEncoded = "";
        String body = "";
        if (headers == null) {
            headers = networkClient.getHeaders();
        }
        for (int i = 0; i < parameterAnnotations.length; i++) {
            if (args[i] == null) continue;
            Annotation[] annotationM = parameterAnnotations[i];
            if (annotationM != null && annotationM.length > 0) {
                Annotation annotation = annotationM[0];
                if (annotation instanceof Path) {
                    path = path.replaceFirst("\\{" + ((Path) annotation).value() + "\\}", args[i].toString());
                } else if (annotation instanceof Query) {
                    vars.put(((Query) annotation).value(), encode(args[i].toString()));
                } else if (annotation instanceof QueryObject) {
                    List<Pair<String, String>> objList =
                            convertObjectToQuery(((QueryObject) annotation).value(), args[i].toString());
                    if (objList != null)
                        for (int k = 0; k < objList.size(); k++) {
                            varList.add(new Pair(objList.get(k).first, encode(objList.get(k).second)));
                        }

                } else if (annotation instanceof Field) {
                    bodyEncoded += "&" + ((Field) annotation).value() + "=" + encode(args[i].toString());
                } else if (annotation instanceof Body) {
                    try {
                        bodyRaw += JsonParser.getJson(args[i]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (!bodyEncoded.isEmpty() && bodyEncoded.startsWith("&")) {
            bodyEncoded = bodyEncoded.substring(1);
        }
        body += bodyEncoded;
        if (!body.isEmpty() && !bodyRaw.isEmpty()) {
            body += "\n";
        }
        body += bodyRaw;
        final Request request = builder.headers(headers)
                .url(NetworkClient.getInstance().getBaseUrl() != null ?
                        NetworkClient.getInstance().getBaseUrl() + path : path)
                .vars(vars)
                .varList(varList)
                .bodyRaw(bodyRaw)
                .bodyEncoded(bodyEncoded)
                .body(body).build();
        return request;
    }

    public NetworkClient networkClient;


    private List<Pair<String, String>> convertObjectToQuery(String mainName, String object) {
        try {
            return JsonParser.toQueryParams(mainName, object);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    public static final String HEAD = "HEAD";
    public static final String PUT = "PUT";


    @SuppressWarnings("unchecked")
    public static <T> T implement(Class int3rface, NetworkClient client) {
        NetworkHandler handler = new NetworkHandler();
        handler.networkClient = client;
        return (T) Proxy.newProxyInstance(
                int3rface.getClassLoader(),
                new Class[]{int3rface},
                handler
        );
    }
}