package com.inappstory.sdk.modulesconnector.utils.filepicker;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import java.util.HashMap;

public interface IFilePicker {
    void setPickerSettings(HashMap<String, Object> settings);

    void show(
            Context context,
            FragmentManager fragmentManager,
            OnFilesChooseCallback callback
    );
}
