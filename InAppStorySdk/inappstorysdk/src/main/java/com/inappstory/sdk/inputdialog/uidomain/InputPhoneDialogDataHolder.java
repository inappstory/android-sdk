package com.inappstory.sdk.inputdialog.uidomain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public final class InputPhoneDialogDataHolder extends InputBaseDialogDataHolder
        implements IInputPhoneDialogDataHolder {
    MutableLiveData<String> mask = new MutableLiveData<>();

    @Override
    public LiveData<String> mask() {
        return mask;
    }

    @Override
    public void setMask(String newMask) {
        mask.postValue(newMask);
    }
}
