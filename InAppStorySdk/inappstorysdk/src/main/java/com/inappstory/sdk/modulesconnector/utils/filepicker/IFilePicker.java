package com.inappstory.sdk.modulesconnector.utils.filepicker;

import androidx.fragment.app.FragmentManager;

import java.util.HashMap;

public interface IFilePicker {
    void setPickerSettings(HashMap<String, Object> settings);

    void show(
            FragmentManager fragmentManager,
            OnFilesChooseCallback callback
    );
}
