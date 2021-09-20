package com.inappstory.sdk.stories.api.models;


import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.network.SerializedName;

/**
 * Created by paperrose on 19.02.2018.
 */

public class StatisticSession {
    public String id;
    @SerializedName("expire_in")
    public int expireIn;
    @SerializedName("is_allow_profiling")
    public boolean allowProfiling;

    public long updatedAt;


    public ArrayList<Integer> viewed = new ArrayList<>();

    public List<List<Object>> statistic = new ArrayList<>();

    public static void setInstance(StatisticSession instance) {
        INSTANCE = instance;
    }

    private static volatile StatisticSession INSTANCE;

    public static StatisticSession getInstance() {
        if (INSTANCE == null) {
            synchronized (StatisticSession.class) {
                if (INSTANCE == null)
                    INSTANCE = new StatisticSession();
            }
        }
        return INSTANCE;
    }


    public static boolean needToUpdate() {
        if (INSTANCE == null) return true;
        if (INSTANCE.id == null || INSTANCE.id.isEmpty()) return true;
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
