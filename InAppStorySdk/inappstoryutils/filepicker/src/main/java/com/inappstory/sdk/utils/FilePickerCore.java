package com.inappstory.sdk.utils;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.modulesconnector.utils.ModuleInitializer;
import com.inappstory.sdk.utils.filepicker.FilePicker;

public class FilePickerCore implements ModuleInitializer {

    @Override
    public void initialize() {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.setFilePicker(new FilePicker());
            }
        });
    }
}
