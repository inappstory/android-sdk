package com.inappstory.sdk.utils;

import android.content.Context;
import android.media.AudioManager;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.stories.utils.AudioModes;

public class AudioManagerUtils {
    private final IASCore core;

    public AudioManagerUtils(IASCore core) {
        this.core = core;
    }

    public void setAudioManagerMode(String mode) {
        AudioManager audioManager = (AudioManager)
                core.appContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioModes.getModeVal(mode));
    }
}
