package com.inappstory.sdk.modulesconnector.utils.filepicker;


public interface OnFilesChooseCallback {
    void onChoose(String[] filesWithTypes);

    void onCancel();

    void onError(String reason);
}
