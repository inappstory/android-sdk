package com.inappstory.sdk.network;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

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

    public static Response doRequest(Request req)
            throws Exception {
        HttpURLConnection connection = (HttpURLConnection) getURL(req).openConnection();
        connection.setRequestMethod(req.getMethod());
        if (req.getHeaders() != null) {
            for (Object key : req.getHeaders().keySet()) {
                connection.setRequestProperty(key.toString(), req.getHeader(key));
            }
        }
        if (req.isFormEncoded()) {
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        }
        if (!req.getMethod().equals(GET) && !req.getBody().isEmpty()) {
            if (!req.isFormEncoded()) {
                connection.setRequestProperty("Content-Type", "application/json");
            }
            connection.setDoOutput(true);
            OutputStream outStream = connection.getOutputStream();
            OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
            outStreamWriter.write(req.getBody());
            outStreamWriter.flush();
            outStreamWriter.close();
            outStream.close();
        }
        int statusCode = connection.getResponseCode();
        Response respObject = null;

        Log.d("InAppStory_Network", connection.getURL().toString() + " \nStatus Code: " + statusCode);
        if (statusCode == 200 || statusCode == 201 || statusCode == 202) {

            String res = getResponseFromStream(connection.getInputStream());
            Log.d("InAppStory_Network", "Response: " + res);
            respObject = new Response.Builder().headers(getHeaders(connection)).code(statusCode).body(res).build();
        } else {
            String res = getResponseFromStream(connection.getErrorStream());
            Log.d("InAppStory_Network", "Error: " + res);
            respObject = new Response.Builder().code(statusCode).errorBody(res).build();
        }
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
        PUT put = method.getAnnotation(PUT.class);
        if (get != null) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                return URLEncoder.encode(var, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
            }
        }
        return var;
    }


    //Test
    public Request generateRequest(String path, Annotation[][] parameterAnnotations, Object[] args, Request.Builder builder) {
        if (networkClient == null) networkClient = NetworkClient.getInstance();
        //
        HashMap<String, String> vars = new HashMap<>();
       // String path = ev.value();
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
                    body += "&" + ((Field) annotation).value() + "=" + encode(args[i].toString());
                } else if (annotation instanceof Body) {
                    try {
                        String bd = JsonParser.getJson(args[i]);
                        body += (body.isEmpty() ? "" : "\n") + bd;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (!body.isEmpty() && body.startsWith("&")) {
            body = body.substring(1);
        }
        final Request request = builder.headers(headers)
                .url(NetworkClient.getInstance().getBaseUrl() != null ?
                        NetworkClient.getInstance().getBaseUrl() + path : path)
                .vars(vars)
                .body(body).build();
        return request;
    }

    public NetworkClient networkClient;



    public static final String POST = "POST";
    public static final String GET = "GET";
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