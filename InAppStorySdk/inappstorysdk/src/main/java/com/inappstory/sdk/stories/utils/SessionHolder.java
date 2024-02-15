package com.inappstory.sdk.stories.utils;


import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.StatisticPermissions;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.utils.ISessionHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SessionHolder implements ISessionHolder {
    private Session session;

    private final Object sessionLock = new Object();

    private final HashMap<String, OldStatisticManager> statisticManagers = new HashMap<>();

    private final ArrayList<Integer> viewed = new ArrayList<>();

    @Override
    public boolean allowStatV1() {
        synchronized (sessionLock) {
            return session != null
                    && session.statisticPermissions != null
                    && session.statisticPermissions.allowStatV1;
        }
    }

    @Override
    public boolean allowStatV2() {
        synchronized (sessionLock) {
            return session != null
                    && session.statisticPermissions != null
                    && session.statisticPermissions.allowStatV2;
        }
    }

    @Override
    public boolean allowProfiling() {
        synchronized (sessionLock) {
            return session != null
                    && session.statisticPermissions != null
                    && session.statisticPermissions.allowProfiling;
        }
    }

    @Override
    public boolean allowCrash() {
        synchronized (sessionLock) {
            return session != null
                    && session.statisticPermissions != null
                    && session.statisticPermissions.allowCrash;
        }
    }

    @Override
    public boolean allowUGC() {
        synchronized (sessionLock) {
            return session != null
                    && session.isAllowUgc;
        }
    }

    @Override
    public String getSessionId() {
        synchronized (sessionLock) {
            return session != null ? session.id : "";
        }
    }

    @Override
    public void setSessionPermissions(StatisticPermissions statisticPermissions) {
        synchronized (sessionLock) {
            if (session != null) session.statisticPermissions = statisticPermissions;
        }
    }

    @Override
    public void setSession(Session session) {
        synchronized (sessionLock) {
            this.session = session;
            if (session != null && session.id != null) {
                statisticManagers.put(session.id, new OldStatisticManager());
            }
            viewed.clear();
        }
    }

    @Override
    public void addViewedId(int id) {
        synchronized (sessionLock) {
            viewed.add(id);
        }
    }

    @Override
    public boolean hasViewedId(int id) {
        synchronized (sessionLock) {
            return viewed.contains(id);
        }
    }

    @Override
    public boolean hasViewedIds() {
        synchronized (sessionLock) {
            return viewed.size() > 0;
        }
    }

    @Override
    public OldStatisticManager currentStatisticManager() {
        synchronized (sessionLock) {
            if (session == null || session.id == null) return null;
            return statisticManagers.get(session.id);
        }
    }

    @Override
    public OldStatisticManager getStatisticManager(String sessionId) {
        synchronized (sessionLock) {
            return statisticManagers.get(sessionId);
        }
    }

    @Override
    public void clear(String oldSessionId) {
        synchronized (sessionLock) {
            viewed.clear();
            if (session != null &&
                    oldSessionId != null &&
                    Objects.equals(session.id, oldSessionId)) {
                statisticManagers.remove(oldSessionId);
                session = null;
            }
        }
    }
}
