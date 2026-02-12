package com.inappstory.sdk.refactoring.session.repositories.datasources.local;

import com.inappstory.sdk.refactoring.session.data.local.SessionDTO;

import java.util.Objects;

public class SessionLocalDataSource {
    private final Object lock = new Object();
    private SessionDTO session;

    public void removeSession(String sessionId) {
        synchronized (lock) {
            if (session != null && Objects.equals(session.sessionId, sessionId)) {
                session = null;
            }
        }
    }

    public SessionDTO getSession() {
        synchronized (lock) {
            return session;
        }
    }
}
