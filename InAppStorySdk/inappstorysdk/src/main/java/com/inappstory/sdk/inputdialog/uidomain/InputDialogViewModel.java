package com.inappstory.sdk.inputdialog.uidomain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.core.models.DialogData;
import com.inappstory.sdk.core.models.js.dialogstructure.DialogStructure;
import com.inappstory.sdk.core.utils.network.JsonParser;
import com.inappstory.sdk.utils.SingleTimeLiveEvent;

public class InputDialogViewModel implements IInputDialogViewModel {
    MutableLiveData<DialogData> currentDialogData = new MutableLiveData<>();
    SingleTimeLiveEvent<KeyboardState> keyboardState = new SingleTimeLiveEvent<>();

    SingleTimeLiveEvent<InputDialogActionData> actionData = new SingleTimeLiveEvent<>();

    public void closeDialog() {
        keyboardState.postValue(KeyboardState.CLOSE);
        currentDialogData.postValue(null);
    }

    public void cancelDialog() {
        DialogData dialogData = currentDialogData().getValue();
        if (dialogData != null) {
            actionData.postValue(new InputDialogActionData(dialogData.widgetId()));
        }
        closeDialog();
    }

    public void sendDialog(String data) {
        DialogData dialogData = currentDialogData().getValue();
        if (dialogData != null) {
            actionData.postValue(new InputDialogActionData(data, dialogData.widgetId()));
        }
        closeDialog();
    }

    @Override
    public LiveData<KeyboardState> keyboardState() {
        return keyboardState;
    }

    @Override
    public LiveData<DialogData> currentDialogData() {
        return currentDialogData;
    }

    public void openDialog(String currentDialogStructure, String widgetId, int storyId) {
        try {
            DialogStructure structure = JsonParser.fromJson(
                    currentDialogStructure, DialogStructure.class
            );
            if (structure != null) {
                currentDialogData.postValue(
                        new DialogData(
                                structure,
                                widgetId,
                                storyId
                        )
                );
            }
        } catch (Exception ignored) {

        }
    }
}
