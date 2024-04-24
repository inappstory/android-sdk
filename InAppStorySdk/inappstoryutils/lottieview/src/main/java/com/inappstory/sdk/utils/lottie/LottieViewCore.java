package com.inappstory.sdk.utils.lottie;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.modulesconnector.utils.ModuleInitializer;
import com.inappstory.sdk.modulesconnector.utils.lottie.ILottieView;
import com.inappstory.sdk.modulesconnector.utils.lottie.ILottieViewGenerator;

public class LottieViewCore implements ModuleInitializer {


    @Override
    public void initialize() {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.lottieViewGenerator = new ILottieViewGenerator() {
                    @Override
                    public ILottieView getView(Context context) {
                        return new LottiePlayerView(context);
                    }
                };
            }
        });
    }
}
