package com.inappstory.sdk;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.inappstory.sdk.utils.IVibrateUtils;

class VibrateUtils implements IVibrateUtils {
    private Vibrator vibrator;

    public void vibrate(Context context, int[] vibratePattern) {
        if (vibrator != null) vibrator.cancel();
        else {
            if (context == null) return;
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (vibratePattern.length == 0) return;
        if (vibratePattern.length > 1) {
            long[] longPattern = new long[vibratePattern.length + 1];
            longPattern[0] = 0;
            for (int i = 1; i <= vibratePattern.length; i++) {
                longPattern[i] = vibratePattern[i - 1];
            }
            vibrator.vibrate(longPattern, -1);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(vibratePattern[0],
                        VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(vibratePattern[0]);
            }
        }
    }

}
