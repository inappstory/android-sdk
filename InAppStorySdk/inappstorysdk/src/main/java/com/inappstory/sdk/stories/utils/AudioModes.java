package com.inappstory.sdk.stories.utils;

public class AudioModes {
    public static int getModeVal(String modeName) {
        switch (modeName) {
            case MODE_CALL_SCREENING:
                return 4;
            case MODE_CURRENT:
                return -1;
            case MODE_INVALID:
                return -2;
            case MODE_IN_CALL:
                return 2;
            case MODE_IN_COMMUNICATION:
                return 3;
            case MODE_RINGTONE:
                return 1;
            default:
                return 0;

        }
    }

    public static final String MODE_CALL_SCREENING = "MODE_CALL_SCREENING";
    public static final String MODE_CURRENT = "MODE_CURRENT";
    public static final String MODE_INVALID = "MODE_INVALID";
    public static final String MODE_IN_CALL = "MODE_IN_CALL";
    public static final String MODE_IN_COMMUNICATION = "MODE_IN_COMMUNICATION";
    public static final String MODE_NORMAL = "MODE_NORMAL";
    public static final String MODE_RINGTONE = "MODE_RINGTONE";
}
