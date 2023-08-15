package com.inappstory.sdk.newnetwork;

import android.util.Pair;


import com.inappstory.sdk.network.DELETE;
import com.inappstory.sdk.network.FormUrlEncoded;
import com.inappstory.sdk.network.GET;
import com.inappstory.sdk.network.POST;
import com.inappstory.sdk.network.PUT;
import com.inappstory.sdk.newnetwork.annotations.api.Body;
import com.inappstory.sdk.newnetwork.annotations.api.Field;
import com.inappstory.sdk.newnetwork.annotations.api.Path;
import com.inappstory.sdk.newnetwork.annotations.api.Query;
import com.inappstory.sdk.newnetwork.annotations.api.QueryObject;
import com.inappstory.sdk.newnetwork.models.Request;
import com.inappstory.sdk.newnetwork.parser.JsonParser;
import com.inappstory.sdk.newnetwork.utils.ObjectToQuery;
import com.inappstory.sdk.newnetwork.utils.UrlEncoder;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class NetworkHandler implements InvocationHandler {
    public NetworkHandler() {
    }

    private Request generateRequest(
            String path,
            Annotation[][] parameterAnnotations,
            Object[] args,
            Request.Builder builder
    ) {
        HashMap<String, String> vars = new HashMap<>();
        ArrayList<Pair<String, String>> varList = new ArrayList<>();
        String bodyRaw = "";
        String bodyEncoded = "";
        String body = "";
        UrlEncoder encoder = new UrlEncoder();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            if (args[i] == null) continue;
            Annotation[] annotationM = parameterAnnotations[i];
            if (annotationM != null && annotationM.length > 0) {
                Annotation annotation = annotationM[0];
                if (annotation instanceof Path) {
                    path = path.replaceFirst("\\{" + ((Path) annotation).value() + "\\}", args[i].toString());
                } else if (annotation instanceof Query) {
                    vars.put(((Query) annotation).value(), encoder.encode(args[i].toString()));
                } else if (annotation instanceof QueryObject) {
                    List<Pair<String, String>> objList =
                            new ObjectToQuery().convert(((QueryObject) annotation).value(), args[i].toString());
                    if (objList != null)
                        for (int k = 0; k < objList.size(); k++) {
                            varList.add(new Pair(objList.get(k).first, encoder.encode(objList.get(k).second)));
                        }

                } else if (annotation instanceof Field) {
                    bodyEncoded += "&" + ((Field) annotation).value() + "=" + encoder.encode(args[i].toString());
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
        final Request request = builder.headers(NetworkClient.getInstance().getHeaders())
                .url(NetworkClient.getInstance().getBaseUrl() != null ?
                        NetworkClient.getInstance().getBaseUrl() + path : path)
                .vars(vars)
                .varList(varList)
                .bodyRaw(bodyRaw)
                .bodyEncoded(bodyEncoded)
                .body(body).build();
        return request;
    }

    @Override
    public Request invoke(Object proxy, Method method, Object[] args) throws Throwable {
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


    @SuppressWarnings("unchecked")
    public static <T> T implement(Class int3rface) {
        NetworkHandler handler = new NetworkHandler();
        return (T) Proxy.newProxyInstance(
                int3rface.getClassLoader(),
                new Class[]{int3rface},
                handler
        );
    }
}
