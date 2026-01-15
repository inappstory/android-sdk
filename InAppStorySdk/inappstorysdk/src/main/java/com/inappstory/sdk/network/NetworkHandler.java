package com.inappstory.sdk.network;

import android.util.Pair;


import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
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
import com.inappstory.sdk.network.annotations.api.QueryVars;
import com.inappstory.sdk.network.annotations.api.ReplaceHeader;
import com.inappstory.sdk.network.utils.ObjectToQuery;
import com.inappstory.sdk.utils.UrlEncoder;
import com.inappstory.sdk.network.utils.headers.AcceptEncodingHeader;
import com.inappstory.sdk.network.utils.headers.AcceptHeader;
import com.inappstory.sdk.network.utils.headers.AcceptLanguageHeader;
import com.inappstory.sdk.network.utils.headers.AuthSessionIdHeader;
import com.inappstory.sdk.network.utils.headers.AuthorizationHeader;
import com.inappstory.sdk.network.utils.headers.ContentTypeHeader;
import com.inappstory.sdk.network.utils.headers.Header;
import com.inappstory.sdk.network.utils.headers.HeadersKeys;
import com.inappstory.sdk.network.utils.headers.MutableHeader;
import com.inappstory.sdk.network.utils.headers.UserAgentHeader;
import com.inappstory.sdk.network.utils.headers.XAppPackageIdHeader;
import com.inappstory.sdk.network.utils.headers.XDeviceIdHeader;
import com.inappstory.sdk.network.utils.headers.XRequestIdHeader;
import com.inappstory.sdk.network.utils.headers.XUserIdHeader;
import com.inappstory.sdk.network.models.Request;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public final class NetworkHandler implements InvocationHandler {


    String baseUrl;
    private final IASCore core;

    public NetworkHandler(String baseUrl, IASCore core) {
        this.baseUrl = baseUrl;
        this.core = core;
    }

    private Request generateRequest(
            String path,
            Annotation[][] parameterAnnotations,
            String[] exclude,
            Object[] args,
            Request.Builder builder,
            boolean isFormEncoded
    ) throws Exception {
        HashMap<String, String> vars = new HashMap<>();
        ArrayList<Pair<String, String>> varList = new ArrayList<>();
        String bodyRaw = "";
        String bodyEncoded = "";
        String body = "";
        UrlEncoder encoder = new UrlEncoder();
        List<Pair<String, String>> replacedHeaders = new ArrayList<>();
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
                    if (objList != null) {
                        for (int k = 0; k < objList.size(); k++) {
                            varList.add(new Pair(objList.get(k).first, encoder.encode(objList.get(k).second)));
                        }
                    }
                } else if (annotation instanceof QueryVars) {
                    List<Pair<String, String>> objList =
                            new ObjectToQuery().convert(args[i].toString());
                    if (objList != null) {
                        for (int k = 0; k < objList.size(); k++) {
                            varList.add(new Pair(objList.get(k).first, encoder.encode(objList.get(k).second)));
                        }
                    }
                } else if (annotation instanceof Field) {
                    bodyEncoded += "&" + ((Field) annotation).value() + "=" + encoder.encode(args[i].toString());
                } else if (annotation instanceof Body) {
                    try {
                        bodyRaw += JsonParser.getJson(args[i]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (annotation instanceof ReplaceHeader) {
                    replacedHeaders.add(new Pair<>(((ReplaceHeader) annotation).value(), args[i].toString()));
                }
            }
        }
        if (bodyEncoded.startsWith("&")) {
            bodyEncoded = bodyEncoded.substring(1);
        }
        body += bodyEncoded;
        if (!body.isEmpty() && !bodyRaw.isEmpty()) {
            body += "\n";
        }
        body += bodyRaw;
        try {
            Request request = builder
                    .isFormEncoded(isFormEncoded)
                    .headers(
                            generateHeaders(
                                    exclude,
                                    replacedHeaders,
                                    isFormEncoded,
                                    !body.isEmpty()
                            )
                    )
                    .url(baseUrl != null ?
                            baseUrl + path : path)
                    .vars(vars)
                    .varList(varList)
                    .bodyRaw(bodyRaw)
                    .bodyEncoded(bodyEncoded)
                    .body(body).build();
            return request;
        } catch (Exception e) {
            return null;
        }
    }

    List<Header> generateHeaders(
            String[] exclude,
            List<Pair<String, String>> replaceHeaders,
            boolean isFormEncoded,
            boolean hasBody
    ) {
        IASDataSettingsHolder dataSettingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        String deviceId = dataSettingsHolder.deviceId();
        boolean anonymous = dataSettingsHolder.anonymous();
        List<String> excludeList = Arrays.asList(exclude);
        List<Header> resHeaders = new ArrayList<>();
        if (!excludeList.contains(HeadersKeys.ACCEPT))
            resHeaders.add(new AcceptHeader());
        if (!excludeList.contains(HeadersKeys.ACCEPT_LANGUAGE))
            resHeaders.add(new AcceptLanguageHeader(core));
        if (!excludeList.contains(HeadersKeys.ACCEPT_ENCODING))
            resHeaders.add(new AcceptEncodingHeader());
        if (!excludeList.contains(HeadersKeys.AUTHORIZATION))
            resHeaders.add(new AuthorizationHeader(core.projectSettingsAPI().apiKey()));
        if (!excludeList.contains(HeadersKeys.APP_PACKAGE_ID))
            resHeaders.add(new XAppPackageIdHeader(core.appContext()));
        if (!excludeList.contains(HeadersKeys.AUTH_SESSION_ID)) {
            boolean hasSessionReplace = false;
            for (Pair<String, String> replaceHeader : replaceHeaders) {
                if (replaceHeader.first.equals(HeadersKeys.AUTH_SESSION_ID) && replaceHeader.second != null) {
                    resHeaders.add(new AuthSessionIdHeader(replaceHeader.second));
                    hasSessionReplace = true;
                    break;
                }
            }
            if (!hasSessionReplace) {
                String session = dataSettingsHolder.sessionIdOrEmpty();
                if (session.isEmpty()) {
                    InAppStoryManager.showELog(
                            LoggerTags.IAS_ERROR_TAG,
                            "Session not set"
                    );
                    throw new RuntimeException("Wrong session");
                } else {
                    resHeaders.add(new AuthSessionIdHeader(session));
                }
            }
        }
        if (!excludeList.contains(HeadersKeys.CONTENT_TYPE))
            resHeaders.add(new ContentTypeHeader(isFormEncoded, hasBody));
        if (!excludeList.contains(HeadersKeys.DEVICE_ID) && deviceId != null) {
            boolean hasDeviceIdHeader = false;
            for (Pair<String, String> replaceHeader : replaceHeaders) {
                if (replaceHeader.first.equals(HeadersKeys.DEVICE_ID)) {
                    hasDeviceIdHeader = true;
                    if (replaceHeader.second != null) {
                        resHeaders.add(new XDeviceIdHeader(deviceId));
                    }
                    break;
                }
            }
            if (!hasDeviceIdHeader && !anonymous) {
                resHeaders.add(new XDeviceIdHeader(deviceId));
            }
        }
        if (!excludeList.contains(HeadersKeys.REQUEST_ID))
            resHeaders.add(new XRequestIdHeader());
        if (!excludeList.contains(HeadersKeys.USER_AGENT))
            resHeaders.add(new UserAgentHeader(core));
        if (!excludeList.contains(HeadersKeys.USER_ID))
            resHeaders.add(new XUserIdHeader());
        for (Header header : resHeaders) {
            if (header instanceof MutableHeader) {
                for (Pair<String, String> replaceHeader : replaceHeaders) {
                    if (header.getKey().equals(replaceHeader.first) && replaceHeader.second != null) {
                        ((MutableHeader) header).setValue(replaceHeader.second);
                    }
                }
                if (header.getKey().equals(HeadersKeys.USER_ID)) {
                    if (header.getValue() != null)
                        ((MutableHeader) header).setValue(header.getValue());
                }
            }
        }
        return resHeaders;
    }

    @Override
    public Request invoke(Object proxy, Method method, Object[] args) throws Exception {
        GET get = method.getAnnotation(GET.class);
        POST post = method.getAnnotation(POST.class);
        DELETE delete = method.getAnnotation(DELETE.class);
        PUT put = method.getAnnotation(PUT.class);
        ExcludeHeaders excludeHeaders = method.getAnnotation(ExcludeHeaders.class);
        String[] exclude = {};
        if (excludeHeaders != null) {
            exclude = excludeHeaders.value();
        }
        Request generatedRequest;
        if (delete != null) {
            generatedRequest = generateRequest(
                    delete.value(),
                    method.getParameterAnnotations(),
                    exclude,
                    args,
                    (new Request.Builder()).delete(),
                    false
            );
        } else if (get != null) {
            generatedRequest = generateRequest(
                    get.value(),
                    method.getParameterAnnotations(),
                    exclude,
                    args,
                    (new Request.Builder()).get(),
                    false
            );
        } else {
            boolean encoded = (method.getAnnotation(FormUrlEncoded.class) != null);
            if (post != null) {
                generatedRequest = generateRequest(
                        post.value(),
                        method.getParameterAnnotations(),
                        exclude,
                        args,
                        (new Request.Builder()).post(),
                        encoded
                );
            } else if (put != null) {
                generatedRequest = generateRequest(
                        put.value(),
                        method.getParameterAnnotations(),
                        exclude,
                        args,
                        (new Request.Builder()).put(),
                        encoded
                );
            } else {
                throw new IllegalStateException("Don't know what to do.");
            }
        }
        if (generatedRequest != null) {
            return generatedRequest;
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public <T> T implement(Class int3rface) {
        return (T) Proxy.newProxyInstance(
                int3rface.getClassLoader(),
                new Class[]{
                        int3rface
                },
                this
        );
    }
}
