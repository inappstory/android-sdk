package com.inappstory.sdk.modulesconnector.utils.filepicker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;

public class DummyFilePicker implements IFilePicker {
    @Override
    public void setPickerSettings(String settings) {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void show(Context context, FragmentManager fragmentManager, int containerId, OnFilesChooseCallback callback) {

    }

    @Override
    public void close() {

    }

}
