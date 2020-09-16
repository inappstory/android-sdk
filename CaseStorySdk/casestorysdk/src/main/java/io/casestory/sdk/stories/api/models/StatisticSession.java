package io.casestory.sdk.stories.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by paperrose on 19.02.2018.
 */

public class StatisticSession {
    public String id;
    @SerializedName("expire_in")
    public int expireIn;

    public long updatedAt;


    public ArrayList<Integer> viewed = new ArrayList<>();

    private static StatisticSession INSTANCE;

    public static boolean needToUpdate() {
        if (INSTANCE == null) return true;
        if (INSTANCE.id == null || INSTANCE.id.isEmpty()) return true;
        if (((System.currentTimeMillis() - INSTANCE.updatedAt) / 1000) > (INSTANCE.expireIn/2)) {
            return true;
        }
        return false;
    }

    public static void updateStatistic() {
        if (INSTANCE == null) return;
        INSTANCE.updatedAt = System.currentTimeMillis();
    }

    public static StatisticSession getInstance() {
        if (INSTANCE == null) return new StatisticSession();
        return INSTANCE;
    }

    public static void clear() {
        INSTANCE = null;

    }

    public void save() {
        updatedAt = System.currentTimeMillis();
        INSTANCE = this;
    }
}
