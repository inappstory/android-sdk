package com.inappstory.sdk.stories.outercallbacks.common.objects;


import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageMainFragment;


public class DefaultOpenInAppMessageReader implements IOpenInAppMessageReader {
    public final static String IN_APP_MESSAGE_FRAGMENT = "IAM_MAIN_FRAGMENT";

    @Override
    public void onOpen(
            IInAppMessage inAppMessage,
            boolean showOnlyIfLoaded,
            FragmentManager fragmentManager,
            int containerId,
            InAppMessageScreenActions screenActions
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
        } catch (Exception e) {

        }
    }
}
