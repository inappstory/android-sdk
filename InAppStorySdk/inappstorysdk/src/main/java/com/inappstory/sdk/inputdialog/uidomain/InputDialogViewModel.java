package com.inappstory.sdk.inputdialog.uidomain;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.core.models.DialogData;
import com.inappstory.sdk.core.models.DialogType;
import com.inappstory.sdk.core.models.js.dialogstructure.DialogStructure;
import com.inappstory.sdk.core.utils.network.JsonParser;
import com.inappstory.sdk.utils.SingleTimeLiveEvent;

public class InputDialogViewModel implements IInputDialogViewModel {
    MutableLiveData<DialogData> currentDialogData = new MutableLiveData<>();
    SingleTimeLiveEvent<KeyboardState> keyboardState = new SingleTimeLiveEvent<>();

    SingleTimeLiveEvent<InputDialogActionData> actionData = new SingleTimeLiveEvent<>();

    @Override
    public void closeDialog() {
        keyboardState.postValue(KeyboardState.CLOSE);
        currentDialogData.postValue(null);
    }


    @Override
    public void cancelDialog() {
        DialogData dialogData = currentDialogData().getValue();
        if (dialogData != null) {
            actionData.postValue(InputDialogActionData.cancel(dialogData.widgetId()));
        }
        closeDialog();
    }

    @Override
    public void validateAndSendDialog(String data, int maskLength) {
        DialogData dialogData = currentDialogData().getValue();
        if (dialogData != null) {
            if (validate(dialogData.dialogType(), data, maskLength)) {
                sendDialog(data);
            } else {
                error(null);
            }
        }
    }

    private boolean validate(DialogType type, String data, int maskLength) {
        switch (type) {
            case MAIL:
                return validateMail(data);
            case PHONE:
                return validatePhone(data, maskLength);
            default:
                return true;
        }
    }

    private boolean validatePhone(String data, int maskLength) {
        return ((maskLength > 0 && data.length() == maskLength)
                ||
                (data != null && data.length() >= 5 && data.length() <= 30));
    }

    private boolean validateMail(String data) {
        return !TextUtils.isEmpty(data)
                && Patterns.EMAIL_ADDRESS.matcher(data).matches();
    }

    private void sendDialog(String data) {
        DialogData dialogData = currentDialogData().getValue();
        if (dialogData != null) {
            actionData.postValue(InputDialogActionData.send(data, dialogData.widgetId()));
        }
        closeDialog();
    }

    private void error(String error) {
        DialogData dialogData = currentDialogData().getValue();
        if (dialogData != null) {
            actionData.postValue(InputDialogActionData.error(error, dialogData.widgetId()));
        }
    }

    @Override
    public LiveData<KeyboardState> keyboardState() {
        return keyboardState;
    }

    @Override
    public LiveData<DialogData> currentDialogData() {
        return currentDialogData;
    }

    @Override
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
