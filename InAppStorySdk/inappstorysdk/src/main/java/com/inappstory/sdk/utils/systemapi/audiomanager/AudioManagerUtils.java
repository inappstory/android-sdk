package com.inappstory.sdk.utils.systemapi.audiomanager;

import android.content.Context;
import android.media.AudioManager;

import com.inappstory.sdk.core.IASCore;

public class AudioManagerUtils implements IAudioManagerUtils {
    private final IASCore core;

    public AudioManagerUtils(IASCore core) {
        this.core = core;
    }

    @Override
    public int pausePlayback(AudioManager.OnAudioFocusChangeListener audioFocusChangeListener) {
        if (audioFocusChangeListener == null) return 0;
        AudioManager am = (AudioManager) core.appContext().getSystemService(Context.AUDIO_SERVICE);
        return am.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
        );
    }
}
