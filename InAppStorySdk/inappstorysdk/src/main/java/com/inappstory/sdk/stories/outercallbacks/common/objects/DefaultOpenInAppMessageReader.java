package com.inappstory.sdk.stories.outercallbacks.common.objects;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.BaseIAMScreen;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageCloseAction;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageMainFragment;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageMainView;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageOpenAction;


public class DefaultOpenInAppMessageReader implements IOpenInAppMessageReader {
    public final static String IN_APP_MESSAGE_FRAGMENT = "IAM_MAIN_FRAGMENT";

    @Override
    public void onOpenInFragment(
            FragmentManager fragmentManager,
            int containerId,
            final InAppMessageScreenActions screenActions
    ) {
        try {
            InAppMessageMainFragment inAppMessageFragment =
                    new InAppMessageMainFragment();
            FragmentTransaction t = fragmentManager.beginTransaction()
                    .add(
                            containerId,
                            inAppMessageFragment,
                            IN_APP_MESSAGE_FRAGMENT
                    );
            t.commit();
            if (screenActions != null) {
                inAppMessageFragment.setOnOpenAction(new InAppMessageOpenAction() {
                    @Override
                    public void onOpen() {
                        screenActions.readerIsOpened();
                    }
                });
                inAppMessageFragment.setOnCloseAction(new InAppMessageCloseAction() {
                    @Override
                    public void onClose() {
                        screenActions.readerIsClosed();
                    }
                });
            }
        } catch (Exception e) {

        }
    }

    @Override
    public BaseIAMScreen onOpenInLayout(
            Context context,
            InAppMessageScreenActions screenActions
    ) {
        try {
            InAppMessageMainView inAppMessageView =
                    new InAppMessageMainView(context);
            inAppMessageView.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    )
            );
            if (screenActions != null) {
                inAppMessageView.setOnOpenAction(new InAppMessageOpenAction() {
                    @Override
                    public void onOpen() {
                        screenActions.readerIsOpened();
                    }
                });
                inAppMessageView.setOnCloseAction(new InAppMessageCloseAction() {
                    @Override
                    public void onClose() {
                        screenActions.readerIsClosed();
                    }
                });
            }
            return inAppMessageView;
        } catch (Exception e) {
            return null;
        }


    }
}
