package com.inappstory.sdk.stories.ui.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.R;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.dialogstructure.DialogStructure;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;

public class ContactDialogCreator {


    public DialogStructure dialogStructure;
    String dialogId;
    int storyId;


    ShowListener showListener;
    SendListener sendListener;
    CancelListener cancelListener;

    public ContactDialogCreator(
            int storyId,
            String dialogId,
            String data,
            ShowListener showListener,
            SendListener sendListener,
            CancelListener cancelListener
    ) {
        this.dialogStructure = JsonParser.fromJson(data, DialogStructure.class);
        this.dialogId = dialogId;
        this.storyId = storyId;
        this.sendListener = sendListener;
        this.showListener = showListener;
        this.cancelListener = cancelListener;
    }

    public void showDialog(final BaseStoryScreen readerScreen) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                showDialogInner(readerScreen);
            }
        });
    }

    private void showDialogInner(BaseStoryScreen readerScreen) {
        if (readerScreen == null) {
            this.cancelListener.onCancel(dialogId);
            return;
        }
        FragmentManager parentFragmentManager = readerScreen.getScreenFragmentManager();
        Fragment oldFragment =
                parentFragmentManager.findFragmentById(R.id.ias_dialog_container);
        if (oldFragment != null) {
            this.cancelListener.onCancel(dialogId);
            return;
        }
        ContactDialogFragment fragment = new ContactDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(dialogStructure.getSerializableKey(), dialogStructure);
        arguments.putInt("storyId", storyId);
        arguments.putString("dialogId", dialogId);
        fragment.setArguments(arguments);
        FragmentTransaction t = parentFragmentManager.beginTransaction()
                .replace(R.id.ias_dialog_container, fragment);
        t.addToBackStack(ContactDialogFragment.TAG);
        t.commit();
        fragment.sendListener = this.sendListener;
        fragment.cancelListener = this.cancelListener;
        this.showListener.onShow();
    }
}
