package com.inappstory.sdk.inputdialog.uidomain;

import androidx.lifecycle.LiveData;

public interface IInputPhoneDialogDataHolder extends IInputBaseDialogDataHolder {
    LiveData<String> mask();

    void setMask(String newMask);
}
