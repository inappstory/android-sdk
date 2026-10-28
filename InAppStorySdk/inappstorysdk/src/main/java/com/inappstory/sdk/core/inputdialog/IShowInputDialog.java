package com.inappstory.sdk.core.inputdialog;


import android.content.Context;

public interface IShowInputDialog {
    void onShow(
            Context context,
            InputDialogData dialogData,
            InputDialogSource source,
            IInputDialogActions submitCallback
    );
}
