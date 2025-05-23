package com.inappstory.sdk.core.ui.screens;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;

import java.util.Locale;

public class IASActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) {
            super.attachBaseContext(newBase);
            return;
        }
        IASCore core = inAppStoryManager.iasCore();
        Locale lang = ((IASDataSettingsHolder)core.settingsAPI()).lang();
        boolean changeLayoutDirection = ((IASDataSettingsHolder)core.settingsAPI()).changeLayoutDirection();
        if (!changeLayoutDirection) {
            super.attachBaseContext(newBase);
            return;
        }
        Resources resources = newBase.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(lang);
        configuration.setLayoutDirection(lang);
        if (Build.VERSION.SDK_INT >= 26) {
            super.attachBaseContext(newBase.createConfigurationContext(configuration));
        } else {
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            super.attachBaseContext(newBase);
        }

    }
}
