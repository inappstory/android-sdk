package com.inappstory.sdk.stories.api.models;


import com.inappstory.sdk.network.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paperrose on 19.02.2018.
 */

public class Session {
    public String id;
    @SerializedName("expire_in")
    public int expireIn;
    @SerializedName("is_allow_profiling")
    public StatisticPermissions statisticPermissions;

    public SessionEditor editor;
    public long updatedAt;

    public boolean isAllowProfiling() {
        synchronized (Session.class) {
            return statisticPermissions != null && statisticPermissions.allowProfiling;
        }
    }

    public ArrayList<Integer> viewed = new ArrayList<>();

    public List<List<Object>> statistic = new ArrayList<>();

    public static void setInstance(Session instance) {
        INSTANCE = instance;
    }

    private static volatile Session INSTANCE;

    public static Session getInstance() {
        if (INSTANCE == null) {
            synchronized (Session.class) {
                if (INSTANCE == null)
                    INSTANCE = new Session();
            }
        }
        return INSTANCE;
    }


    public static boolean needToUpdate() {
        synchronized (Session.class) {
            if (INSTANCE == null) return true;
            if (INSTANCE.id == null || INSTANCE.id.isEmpty()) return true;
        }
        return false;
    }

    public static void updateStatistic() {
        if (INSTANCE == null) return;
        INSTANCE.updatedAt = System.currentTimeMillis();
    }

    public static void clear() {
        INSTANCE = null;
    }

    public void save() {
        updatedAt = System.currentTimeMillis();
        INSTANCE = this;
        //SharedPreferencesAPI.ge
    }
}