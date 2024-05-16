package com.inappstory.sdk.modulesconnector.utils.filepicker;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import java.util.HashMap;

public interface IFilePicker {
    void setPickerSettings(String settings);

    boolean onBackPressed();

    void show(
            Context context,
            FragmentManager fragmentManager,
            int containerId,
            OnFilesChooseCallback callback
    );

    void close();
}
