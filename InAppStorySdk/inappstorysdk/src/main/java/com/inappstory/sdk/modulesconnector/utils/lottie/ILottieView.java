package com.inappstory.sdk.modulesconnector.utils.lottie;

public interface ILottieView {
    void setSource(Object source);

    void play();

    void stop();

    void pause();

    void resume();

    void restart();

    boolean isLooped();

    void setAnimProgress(float progress);

    void setLoop(boolean isLooped);
}
