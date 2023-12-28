package com.inappstory.sdk.inputdialog.uidomain;

import androidx.lifecycle.LiveData;

import com.inappstory.sdk.core.models.js.dialogstructure.DialogStructure;

public interface IInputDialogViewModel {
    DialogStructure dialogStructure();
    LiveData<KeyboardState> keyboardState();
    LiveData<Boolean> dialogIsOpened();
}
