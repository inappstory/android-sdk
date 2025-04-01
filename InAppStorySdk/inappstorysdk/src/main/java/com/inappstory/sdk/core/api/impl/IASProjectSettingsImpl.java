package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASProjectSettingsInternal;

import java.util.Objects;

public class IASProjectSettingsImpl implements IASProjectSettingsInternal {
    private String apiKey;
    private String testKey;
    private String host;
    private String cacheDir;
    private final IASCore core;

    private final Object lock = new Object();

    public IASProjectSettingsImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public String apiKey() {
        synchronized (lock) {
            return apiKey;
        }
    }

    @Override
    public String testKey() {
        synchronized (lock) {
            return testKey;
        }
    }

    @Override
    public String host() {
        synchronized (lock) {
            return host;
        }
    }

    @Override
    public String cacheDir() {
        synchronized (lock) {
            return cacheDir;
        }
    }

    @Override
    public IASProjectSettingsInternal apiKey(String apiKey) {
        synchronized (lock) {
            if (apiKey != null && this.apiKey != null && !apiKey.equals(this.apiKey)) {
                core.network().clear();
            }
            this.apiKey = apiKey;
        }
        return this;
    }

    @Override
    public IASProjectSettingsInternal testKey(String testKey) {
        synchronized (lock) {
            this.testKey = testKey;
            return this;
        }
    }

    @Override
    public IASProjectSettingsInternal host(String host) {
        synchronized (lock) {
            if (this.host != null && host != null && !host.equals(this.host)) {
                core.network().clear();
            }
            this.host = host;
            core.network().setBaseUrl(host);
            return this;
        }
    }

    @Override
    public IASProjectSettingsInternal cacheDir(String dir) {
        synchronized (lock) {
            this.cacheDir = dir;
            return this;
        }
    }
}
