package com.inappstory.sdk.utils.lottie;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.inappstory.sdk.modulesconnector.utils.lottie.ILottieView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class LottiePlayerView extends LottieAnimationView implements ILottieView {
    public LottiePlayerView(Context context) {
        super(context);
    }

    public LottiePlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LottiePlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setSource(Object source) {
        if (source instanceof Pair) {
            Pair<?, ?> casted = (Pair<?, ?>) source;
            if (casted.first instanceof String) {
                if (casted.second instanceof String) {
                    setAnimation(
                            new ByteArrayInputStream(
                                    ((String) casted.second).getBytes()
                            ),
                            (String) casted.first
                    );
                } else if (casted.second instanceof File) {
                    try {
                        setAnimation(
                                new FileInputStream((File) casted.second),
                                (String) casted.first
                        );
                    } catch (FileNotFoundException e) {

                    }
                } else if (casted.second instanceof ZipInputStream) {
                  //  this.setImageAssetsFolder();
                    setAnimation(
                            (ZipInputStream) casted.second,
                            (String) casted.first
                    );
                } if (casted.second instanceof InputStream) {
                    setAnimation(
                            (InputStream) casted.second,
                            (String) casted.first
                    );
                }
            }
        }
        this.setScaleType(ScaleType.CENTER_CROP);
    }

    @Override
    public void play() {
        this.playAnimation();
    }

    @Override
    public void stop() {
        this.pauseAnimation();
    }

    @Override
    public void pause() {
        this.pauseAnimation();
    }

    @Override
    public void resume() {
        this.resumeAnimation();
    }

    @Override
    public void restart() {
        this.playAnimation();
    }

    @Override
    public void setAnimProgress(float progress) {
        this.setProgress(progress);
    }

    @Override
    public void setLoop(boolean isLooped) {
        if (isLooped) this.setRepeatCount(LottieDrawable.INFINITE);
        else this.setRepeatCount(0);
    }
}
