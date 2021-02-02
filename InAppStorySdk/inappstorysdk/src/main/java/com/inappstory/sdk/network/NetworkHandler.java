package com.inappstory.sdk.network;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
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

    private NetworkHandler() {
    }

    public static Response doRequest(Request req)
            throws Exception {
        String url = req.getUrl();
        String varStr = "";
        if (req.getVars() != null && req.getVars().keySet().size() > 0) {
            for (Object key : req.getVarKeys()) {
                varStr += "&" + key + "=" + req.getVars().get(key);
            }
            varStr = "?" + varStr.substring(1);
        }
        URL obj = new URL(url + varStr);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
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

            Log.d("InAppStory_Network", connection.getURL().toString() + " \nBody: " + req.getBody());

            outStreamWriter.write(req.getBody());
            outStreamWriter.flush();
            outStreamWriter.close();
            outStream.close();
        }
        int statusCode = connection.getResponseCode();
        Response respObject = null;

        Log.d("InAppStory_Network", connection.getURL().toString() + " \nStatus Code: " + statusCode);
        if (statusCode == 200 || statusCode == 201 || statusCode == 202) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();


            Log.d("InAppStory_Network", "Success: " + response.toString());
            respObject = new Response.Builder().headers(getHeaders(connection)).code(statusCode).body(response.toString()).build();
        } else {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();
            Log.d("InAppStory_Network", "Error: " + response.toString());
            respObject = new Response.Builder().code(statusCode).errorBody(response.toString()).build();
        }
        connection.disconnect();
        return respObject;
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
            return getRequest(get, method, args);
        } else if (post != null) {
            return postRequest(post, method, args);
        } else if (put != null) {
            return putRequest(put, method, args);
        } else {
            throw new IllegalStateException("Don't know what to do.");
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

    private Request getRequest(GET ev, Method method, Object[] args) {
        HashMap<String, String> vars = new HashMap<>();
        String path = ev.value();
        if (headers == null) {
            headers = networkClient.getHeaders();
        }
        for (int i = 0; i < method.getParameterAnnotations().length; i++) {
            if (args[i] == null) continue;
            Annotation[] annotationM = method.getParameterAnnotations()[i];
            if (annotationM != null && annotationM.length > 0) {
                Annotation annotation = annotationM[0];
                if (annotation instanceof Path) {
                    path = path.replaceFirst("\\{" +  ((Path) annotation).value()+ "\\}", args[i].toString());
                } else if (annotation instanceof Query) {
                    String val = args[i].toString();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        try {
                            val = URLEncoder.encode(val, StandardCharsets.UTF_8.toString());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    vars.put(((Query) annotation).value(), val);
                }
            }
        }
        final Request request = (new Request.Builder()).get().headers(headers).url(NetworkClient.getInstance().getBaseUrl() + path).vars(vars).build();
        return request;
    }


    private Request postRequest(POST ev, Method method, Object[] args) {

        boolean encoded = (method.getAnnotation(FormUrlEncoded.class) != null);
        HashMap<String, String> vars = new HashMap<>();
        String path = ev.value();
        String body = "";
        if (headers == null) {
            headers = networkClient.getHeaders();
        }
        for (int i = 0; i < method.getParameterAnnotations().length; i++) {
            if (args[i] == null) continue;
            Annotation[] annotationM = method.getParameterAnnotations()[i];
            if (annotationM != null && annotationM.length > 0) {
                Annotation annotation = annotationM[0];
                if (annotation instanceof Path) {
                    path = path.replaceFirst("\\{" +  ((Path) annotation).value()+ "\\}", args[i].toString());
                } else if (annotation instanceof Query) {
                    String val = args[i].toString();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        try {
                            val = URLEncoder.encode(val, StandardCharsets.UTF_8.toString());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    vars.put(((Query) annotation).value(), val);
                } else if (annotation instanceof Field) {
                    String val = args[i].toString();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        try {
                            val = URLEncoder.encode(val, StandardCharsets.UTF_8.toString());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    body += "&" + ((Field) annotation).value() + "=" + val;
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
        final Request request = (new Request.Builder()).post().isFormEncoded(encoded).headers(headers)
                .url(NetworkClient.getInstance().getBaseUrl() + path)
                .vars(vars)
                .body(body).build();
        return request;
    }

    public NetworkClient networkClient;

    private Request putRequest(PUT ev, Method method, Object[] args) {

        boolean encoded = (method.getAnnotation(FormUrlEncoded.class) != null);
        HashMap<String, String> vars = new HashMap<>();
        String path = ev.value();
        String body = "";
        if (headers == null) {
            headers = networkClient.getHeaders();
        }
        for (int i = 0; i < method.getParameterAnnotations().length; i++) {
            if (args[i] == null) continue;
            Annotation[] annotationM = method.getParameterAnnotations()[i];
            if (annotationM != null && annotationM.length > 0) {
                Annotation annotation = annotationM[0];
                if (annotation instanceof Path) {
                    path = path.replaceFirst("\\{" +  ((Path) annotation).value()+ "\\}", args[i].toString());
                } else if (annotation instanceof Query) {
                    vars.put(((Query) annotation).value(), args[i].toString());
                } else if (annotation instanceof Field) {
                    body += "&" + ((Field) annotation).value() + "=" + args[i].toString();
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
        final Request request = (new Request.Builder()).put().isFormEncoded(encoded).headers(headers)
                .url(NetworkClient.getInstance().getBaseUrl() + path)
                .vars(vars)
                .body(body).build();
        return request;
    }


    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String HEAD = "HEAD";
    public static final String PUT = "PUT";


    private boolean isNumber(Class<?> klass) {
        return klass.isAssignableFrom(Number.class) || klass == int.class || klass == long.class;
    }

    private void thr(Method method) {
        throw new IllegalArgumentException(method.getName() + " method's parameter is not String (for label) or int / long (for value)");
    }

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