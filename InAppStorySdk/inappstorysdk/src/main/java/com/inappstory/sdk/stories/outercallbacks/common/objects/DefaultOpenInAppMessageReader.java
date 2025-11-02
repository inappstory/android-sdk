package com.inappstory.sdk.stories.outercallbacks.common.objects;


import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageCloseAction;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageMainFragment;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageOpenAction;


public class DefaultOpenInAppMessageReader implements IOpenInAppMessageReader {
    public final static String IN_APP_MESSAGE_FRAGMENT = "IAM_MAIN_FRAGMENT";

    @Override
    public void onOpen(
            IInAppMessage inAppMessage,
            boolean showOnlyIfLoaded,
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
            //   t.addToBackStack(IN_APP_MESSAGE_FRAGMENT);
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
            Log.e("ShowIAMLog", System.currentTimeMillis() + "_finish");
        } catch (Exception e) {

        }
    }
}
