package com.inappstory.sdk.modulesconnector.utils.filepicker;

import androidx.fragment.app.FragmentManager;

import java.util.HashMap;

public class DummyFilePicker implements IFilePicker {
    @Override
    public void setPickerSettings(HashMap<String, Object> settings) {

    }

    @Override
    public void show(FragmentManager fragmentManager, OnFilesChooseCallback callback) {

    }

}
