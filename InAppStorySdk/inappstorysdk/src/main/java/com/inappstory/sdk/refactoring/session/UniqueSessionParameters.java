package com.inappstory.sdk.refactoring.session;

import java.util.Locale;
import java.util.Objects;

public class UniqueSessionParameters {

    public UniqueSessionParameters() {

    }

    public UniqueSessionParameters userSign(String userSign) {
        this.userSign = userSign;
        return this;
    }

    public UniqueSessionParameters sendStatistic(boolean sendStatistic) {
        this.sendStatistic = sendStatistic;
        return this;
    }

    public UniqueSessionParameters locale(Locale locale) {
        this.locale = locale.toLanguageTag();
        return this;
    }

    public UniqueSessionParameters userId(String userId) {
        this.userId = userId;
        return this;
    }

    public UniqueSessionParameters anonymous(boolean anonymous) {
        this.anonymous = anonymous;
        return this;
    }

    public String userSign() {
        return userSign;
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
        UniqueSessionParameters that = (UniqueSessionParameters) o;
        return Objects.equals(userSign, that.userSign)
                && Objects.equals(userId, that.userId)
                && Objects.equals(anonymous, that.anonymous)
                && Objects.equals(sendStatistic, that.sendStatistic)
                && Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userSign, userId, locale, anonymous, sendStatistic);
    }

    private String userSign;
    private boolean anonymous;
    private boolean sendStatistic;
    private String userId;
    private String locale;

    @Override
    public String toString() {
        return "UniqueSessionParameters{" +
                "userId='" + userId + '\'' +
                ", userSign='" + userSign + '\'' +
                ", anonymous='" + anonymous + '\'' +
                ", send stat='" + sendStatistic + '\'' +
                ", locale='" + locale + '\'' +
                '}';
    }
}
