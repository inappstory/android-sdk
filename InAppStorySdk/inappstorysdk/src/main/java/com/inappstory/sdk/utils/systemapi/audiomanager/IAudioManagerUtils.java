package com.inappstory.sdk.utils.systemapi.audiomanager;

import android.media.AudioManager;

public interface IAudioManagerUtils {
    int pausePlayback(AudioManager.OnAudioFocusChangeListener audioFocusChangeListener);
}
