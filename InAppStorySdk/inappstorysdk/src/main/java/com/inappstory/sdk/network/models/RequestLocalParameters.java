package com.inappstory.sdk.network.models;

import com.inappstory.sdk.stories.outercallbacks.common.objects.SerializableWithKey;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

public class RequestLocalParameters implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "requestLocalParameters";

    public RequestLocalParameters() {

    }

    public RequestLocalParameters sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public RequestLocalParameters sendStatistic(boolean sendStatistic) {
        this.sendStatistic = sendStatistic;
        return this;
    }

    public RequestLocalParameters locale(Locale locale) {
        this.locale = locale.toLanguageTag();
        return this;
    }

    public RequestLocalParameters userId(String userId) {
        this.userId = userId;
        return this;
    }

    public RequestLocalParameters anonymous(boolean anonymous) {
        this.anonymous = anonymous;
        return this;
    }

    public String sessionId() {
        return sessionId;
    }

    public String locale() {
        return locale;
    }

    public String userId() {
        return userId;
    }

    public boolean anonymous() {
        return anonymous;
    }

    public boolean sendStatistic() {
        return sendStatistic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestLocalParameters that = (RequestLocalParameters) o;
        return Objects.equals(sessionId, that.sessionId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(anonymous, that.anonymous)
                && Objects.equals(sendStatistic, that.sendStatistic)
                && Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, userId, locale, anonymous, sendStatistic);
    }

    private String sessionId;
    private boolean anonymous;
    private boolean sendStatistic;
    private String userId;
    private String locale;

    @Override
    public String toString() {
        return "RequestLocalParameters{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", anonymous='" + anonymous + '\'' +
                ", send stat='" + sendStatistic + '\'' +
                ", locale='" + locale + '\'' +
                '}';
    }

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}
