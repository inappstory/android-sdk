package com.inappstory.sdk.network;

import static java.util.UUID.randomUUID;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequestHeader;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NetworkHandler implements InvocationHandler {
    /**
     * Трекер аналитики
     */

    public NetworkHandler() {
    }

    static URL getURL(Request req) throws Exception {
        String url = req.getUrl();
        String varStr = "";
        if (req.getVars() != null && req.getVars().keySet().size() > 0) {
            for (Object key : req.getVarKeys()) {
                varStr += "&" + key + "=" + req.getVars().get(key);
            }
            varStr = "?" + varStr.substring(1);
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
                requestLog.headers.add(new ApiLogRequestHeader(key.toString(), req.getHeader(key)));
                connection.setRequestProperty(key.toString(), req.getHeader(key));
            }
        }
        if (InAppStoryService.getInstance() != null && InAppStoryService.getInstance().getUserId() != null) {
            connection.setRequestProperty("X-User-id", InAppStoryService.getInstance().getUserId());
            requestLog.headers.add(
                    new ApiLogRequestHeader("X-User-id", InAppStoryService.getInstance().getUserId()));
        }
        connection.setRequestProperty("X-Request-ID", randomUUID().toString());
        requestLog.headers.add(
                new ApiLogRequestHeader("X-Request-ID", connection.getRequestProperty("X-Request-ID")));

        if (req.isFormEncoded()) {
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            requestLog.headers.add(
                    new ApiLogRequestHeader("Content-Type", "application/x-www-form-urlencoded"));
        }
        if (!sessionKill && !Session.needToUpdate() && !req.getUrl().contains("session/open")) {
            connection.setRequestProperty("auth-session-id", Session.getInstance().id);

            requestLog.headers.add(
                    new ApiLogRequestHeader("auth-session-id", Session.getInstance().id));
        }



        InAppStoryManager.showDLog("InAppStory_Network", req.getHeadersString());
        if (!req.getMethod().equals(GET) && !req.getMethod().equals(HEAD) && !req.getBody().isEmpty()) {
            InAppStoryManager.showDLog("InAppStory_Network", req.getBody());
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
        InAppStoryManager.showDLog("InAppStory_Network", connection.getURL().toString() + " \nStatus Code: " + statusCode);
        //apiLog.duration = System.currentTimeMillis() - start;
        long contentLength = 0;

        if (statusCode == 200 || statusCode == 201 || statusCode == 202) {
            String res = getResponseFromStream(connection.getInputStream());
            contentLength = res.length();
            InAppStoryManager.showDLog("InAppStory_Network", "Success: " + res);
            respObject = new Response.Builder().contentLength(contentLength).
                    headers(getHeaders(connection)).code(statusCode).body(res).build();
        } else {
            String res = getResponseFromStream(connection.getErrorStream());
            contentLength = res.length();
            InAppStoryManager.showDLog("InAppStory_Network", "Error: " + res);
            respObject = new Response.Builder().contentLength(contentLength).
                    headers(getHeaders(connection)).code(statusCode).errorBody(res).build();
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
    public static String getResponseFromStream(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        return response.toString();
    }

    private static String mockFeed() {
        return "{\"id\":1,\"hasFavorite\":false,\"stories\":[{\"title\":\"Сберзвук звукомания\",\"title_color\":\"#ffffff\",\"source\":\"test номер 1\",\"image\":[{\"url\":\"https://cdn.test.inappstory.com/np/story/jv9/mec/tiw/0icmemejzjs8enjlxh1w4zj/custom_cover/logo-220x220.webp?v=1668762302\",\"width\":220,\"height\":220,\"type\":\"m\"},{\"url\":\"https://cdn.test.inappstory.com/np/story/jv9/mec/tiw/0icmemejzjs8enjlxh1w4zj/custom_cover/logo-440x440.webp?v=1668762302\",\"width\":440,\"height\":440,\"type\":\"h\"}],\"background_color\":\"#209bbb\",\"display_to\":null,\"display_from\":1668424988,\"updated_at\":1668762302,\"is_opened\":true,\"like\":0,\"favorite\":false,\"slides_count\":1,\"deeplink\":null,\"disable_close\":false,\"like_functional\":true,\"favorite_functional\":true,\"share_functional\":true,\"tags\":[],\"has_audio\":false,\"video_cover\":null,\"need_placeholders\":false,\"has_swipe_up\":false,\"position\":null,\"string_id\":\"r4nkcpg\",\"hide_in_reader\":false},{\"id\":7630,\"title\":\"5 суперароматных вин (TEST)5 суперароматных вин (TEST)5 суперароматных вин (TEST)5 суперароматных вин (TEST)5 суперароматных вин (TEST)5 суперароматных вин (TEST)5 суперароматных вин (TEST)5 суперароматных вин (TEST)5 суперароматных вин (TEST)5 суперарома\",\"title_color\":\"#ffffff\",\"source\":\"test номер 1\",\"image\":[{\"url\":\"https://cdn.test.inappstory.com/np/story/rlo/c3f/u0b/frdhxpohqiestdxq6zeqxzb/logo-220x220.webp?v=1667997633\",\"width\":220,\"height\":220,\"type\":\"m\"},{\"url\":\"https://cdn.test.inappstory.com/np/story/rlo/c3f/u0b/frdhxpohqiestdxq6zeqxzb/logo-440x440.webp?v=1667997633\",\"width\":440,\"height\":440,\"type\":\"h\"}],\"background_color\":\"#ffffff\",\"display_to\":null,\"display_from\":1667245599,\"updated_at\":1667997633,\"is_opened\":true,\"like\":0,\"favorite\":false,\"slides_count\":6,\"deeplink\":null,\"disable_close\":false,\"like_functional\":true,\"favorite_functional\":true,\"share_functional\":true,\"tags\":[],\"has_audio\":false,\"video_cover\":null,\"need_placeholders\":false,\"has_swipe_up\":false,\"position\":null,\"string_id\":\"puwi9r4\",\"hide_in_reader\":false}]}";
    }

    private static HashMap<String, String> getHeaders(@NonNull final HttpURLConnection connection) {
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
                .bodyRaw(bodyRaw)
                .bodyEncoded(bodyEncoded)
                .body(body).build();
        return request;
    }

    public NetworkClient networkClient;


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