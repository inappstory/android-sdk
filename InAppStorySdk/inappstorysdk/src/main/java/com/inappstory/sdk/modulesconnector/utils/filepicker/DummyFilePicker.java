package com.inappstory.sdk.modulesconnector.utils.filepicker;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import java.util.HashMap;

public class DummyFilePicker implements IFilePicker {
    @Override
    public void setPickerSettings(HashMap<String, Object> settings) {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void show(Context context, FragmentManager fragmentManager, OnFilesChooseCallback callback) {

    }

    @Override
    public void close() {

    }

}
