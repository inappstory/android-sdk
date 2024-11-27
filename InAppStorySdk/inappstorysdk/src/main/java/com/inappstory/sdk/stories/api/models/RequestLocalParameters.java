package com.inappstory.sdk.stories.api.models;

import java.util.Locale;
import java.util.Objects;

public class RequestLocalParameters {
    public RequestLocalParameters(String sessionId, String userId, Locale locale) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.locale = locale.toLanguageTag();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestLocalParameters that = (RequestLocalParameters) o;
        return Objects.equals(sessionId, that.sessionId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, userId, locale);
    }

    public String sessionId;
    public String userId;
    public String locale;
}
