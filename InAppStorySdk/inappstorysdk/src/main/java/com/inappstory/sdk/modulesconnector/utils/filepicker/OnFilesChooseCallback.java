package com.inappstory.sdk.modulesconnector.utils.filepicker;


public interface OnFilesChooseCallback {
    void onChoose(String cbName, String cbId, String[] filesWithTypes);

    void onCancel(String cbName, String cbId);

    void onError(String cbName, String cbId, String reason);
}
