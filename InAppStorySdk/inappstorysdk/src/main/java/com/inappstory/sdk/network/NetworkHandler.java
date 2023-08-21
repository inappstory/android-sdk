package com.inappstory.sdk.network;

import android.content.Context;
import android.util.Pair;


import com.inappstory.sdk.network.annotations.api.Body;
import com.inappstory.sdk.network.annotations.api.DELETE;
import com.inappstory.sdk.network.annotations.api.ExcludeHeaders;
import com.inappstory.sdk.network.annotations.api.Field;
import com.inappstory.sdk.network.annotations.api.FormUrlEncoded;
import com.inappstory.sdk.network.annotations.api.GET;
import com.inappstory.sdk.network.annotations.api.POST;
import com.inappstory.sdk.network.annotations.api.PUT;
import com.inappstory.sdk.network.annotations.api.Path;
import com.inappstory.sdk.network.annotations.api.Query;
import com.inappstory.sdk.network.annotations.api.QueryObject;
import com.inappstory.sdk.network.utils.ObjectToQuery;
import com.inappstory.sdk.network.utils.UrlEncoder;
import com.inappstory.sdk.network.utils.headers.AcceptEncodingHeader;
import com.inappstory.sdk.network.utils.headers.AcceptHeader;
import com.inappstory.sdk.network.utils.headers.AcceptLanguageHeader;
import com.inappstory.sdk.network.utils.headers.AuthSessionIdHeader;
import com.inappstory.sdk.network.utils.headers.AuthorizationHeader;
import com.inappstory.sdk.network.utils.headers.ContentTypeHeader;
import com.inappstory.sdk.network.utils.headers.Header;
import com.inappstory.sdk.network.utils.headers.HeadersKeys;
import com.inappstory.sdk.network.utils.headers.UserAgentHeader;
import com.inappstory.sdk.network.utils.headers.XAppPackageIdHeader;
import com.inappstory.sdk.network.utils.headers.XDeviceIdHeader;
import com.inappstory.sdk.network.utils.headers.XRequestIdHeader;
import com.inappstory.sdk.network.utils.headers.XUserIdHeader;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.models.Response;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class NetworkHandler implements InvocationHandler {

    Context appContext;
    String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public NetworkHandler(String baseUrl, Context appContext) {
        this.baseUrl = baseUrl;
        this.appContext = appContext;
    }

    private Request generateRequest(
            String path,
            Annotation[][] parameterAnnotations,
            Object[] args,
            Request.Builder builder,
            boolean isFormEncoded
    ) {
        HashMap<String, String> vars = new HashMap<>();
        ArrayList<Pair<String, String>> varList = new ArrayList<>();
        String bodyRaw = "";
        String bodyEncoded = "";
        String body = "";
        UrlEncoder encoder = new UrlEncoder();
        String[] exclude = {};
        for (int i = 0; i < parameterAnnotations.length; i++) {
            if (args[i] == null) continue;
            Annotation[] annotationM = parameterAnnotations[i];
            if (annotationM != null && annotationM.length > 0) {
                Annotation annotation = annotationM[0];
                if (annotation instanceof ExcludeHeaders) {
                    exclude = ((ExcludeHeaders) annotation).value();
                } else if (annotation instanceof Path) {
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
        Request request = builder
                .isFormEncoded(isFormEncoded)
                .headers(
                        generateHeaders(appContext, exclude, isFormEncoded, body.isEmpty())
                )
                .url(baseUrl != null ?
                        baseUrl + path : path)
                .vars(vars)
                .varList(varList)
                .bodyRaw(bodyRaw)
                .bodyEncoded(bodyEncoded)
                .body(body).build();
        return request;
    }

    List<Header> generateHeaders(
            Context context,
            String[] exclude,
            boolean isFormEncoded,
            boolean hasBody
    ) {
        List<String> excludeList = Arrays.asList(exclude);
        List<Header> resHeaders = new ArrayList<>();
        if (!excludeList.contains(HeadersKeys.ACCEPT))
            resHeaders.add(new AcceptHeader());
        if (!excludeList.contains(HeadersKeys.ACCEPT_LANGUAGE))
            resHeaders.add(new AcceptLanguageHeader());
        if (!excludeList.contains(HeadersKeys.ACCEPT_ENCODING))
            resHeaders.add(new AcceptEncodingHeader());
        if (!excludeList.contains(HeadersKeys.AUTHORIZATION))
            resHeaders.add(new AuthorizationHeader());
        if (!excludeList.contains(HeadersKeys.APP_PACKAGE_ID))
            resHeaders.add(new XAppPackageIdHeader(context));
        if (!excludeList.contains(HeadersKeys.AUTH_SESSION_ID))
            resHeaders.add(new AuthSessionIdHeader());
        if (!excludeList.contains(HeadersKeys.CONTENT_TYPE))
            resHeaders.add(new ContentTypeHeader(isFormEncoded, hasBody));
        if (!excludeList.contains(HeadersKeys.DEVICE_ID))
            resHeaders.add(new XDeviceIdHeader(context));
        if (!excludeList.contains(HeadersKeys.REQUEST_ID))
            resHeaders.add(new XRequestIdHeader());
        if (!excludeList.contains(HeadersKeys.USER_AGENT))
            resHeaders.add(new UserAgentHeader(context));
        if (!excludeList.contains(HeadersKeys.USER_ID))
            resHeaders.add(new XUserIdHeader());
        return resHeaders;
    }

    @Override
    public Request invoke(Object proxy, Method method, Object[] args) throws Throwable {
        GET get = method.getAnnotation(GET.class);
        POST post = method.getAnnotation(POST.class);
        DELETE delete = method.getAnnotation(DELETE.class);
        PUT put = method.getAnnotation(PUT.class);
        if (delete != null) {
            return generateRequest(delete.value(), method.getParameterAnnotations(), args, (new Request.Builder()).delete(), false);
        } else if (get != null) {
            return generateRequest(get.value(), method.getParameterAnnotations(), args, (new Request.Builder()).get(), false);
        } else {
            boolean encoded = (method.getAnnotation(FormUrlEncoded.class) != null);
            if (post != null) {
                return generateRequest(post.value(), method.getParameterAnnotations(), args, (new Request.Builder()).post(), encoded);
            } else if (put != null) {
                return generateRequest(put.value(), method.getParameterAnnotations(), args, (new Request.Builder()).put(), encoded);
            } else {
                throw new IllegalStateException("Don't know what to do.");
            }
        }
    }


    @SuppressWarnings("unchecked")
    public <T> T implement(Class int3rface) {
        return (T) Proxy.newProxyInstance(
                int3rface.getClassLoader(),
                new Class[] {
                        int3rface
                },
                this
        );
    }
}
