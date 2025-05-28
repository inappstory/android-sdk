package com.inappstory.sdk.stories.api.models;

public class UpdateTimelineData {
    public String action;
    public long currentTime;
    public long duration;
    public int slideIndex;
    public boolean showLoader;
    public boolean showError;

    @Override
    public String toString() {
        return "UpdateTimelineData{" +
                "action='" + action + '\'' +
                ", currentTime=" + currentTime +
                ", duration=" + duration +
                ", slideIndex=" + slideIndex +
                ", showLoader=" + showLoader +
                ", showError=" + showError +
                '}';
    }
}
