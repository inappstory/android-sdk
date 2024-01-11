package com.inappstory.sdk.inputdialog.uidomain;

import androidx.lifecycle.LiveData;

import com.inappstory.sdk.core.models.DialogData;
import com.inappstory.sdk.core.models.js.dialogstructure.DialogStructure;

public interface IInputDialogViewModel {
    LiveData<KeyboardState> keyboardState();
    LiveData<DialogData> currentDialogData();

    void openDialog(String currentDialogStructure, String widgetId, int storyId);
    void closeDialog();
    void cancelDialog();
    void sendDialog(String data);
}
