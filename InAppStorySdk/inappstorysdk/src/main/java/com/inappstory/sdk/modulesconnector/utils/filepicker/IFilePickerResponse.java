package com.inappstory.sdk.modulesconnector.utils.filepicker;

public interface IFilePickerResponse {
    String id();
    String cb();
    String accept();
    boolean multiple();
    String config();
}
