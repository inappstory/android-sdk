package com.inappstory.sdk.inputdialog.uidomain;

import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.core.models.js.dialogstructure.DialogStructure;
import com.inappstory.sdk.utils.SingleTimeLiveEvent;

public class InputDialogViewModel {
    MutableLiveData<Boolean> dialogIsOpened = new MutableLiveData<>();
    private DialogStructure currentDialogStructure;
    SingleTimeLiveEvent<KeyboardState> keyboardState = new SingleTimeLiveEvent<>();
}
