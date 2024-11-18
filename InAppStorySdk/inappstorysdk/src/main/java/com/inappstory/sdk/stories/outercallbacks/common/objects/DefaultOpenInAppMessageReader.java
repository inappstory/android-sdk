package com.inappstory.sdk.stories.outercallbacks.common.objects;


import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageMainFragment;


public class DefaultOpenInAppMessageReader implements IOpenInAppMessageReader {


    @Override
    public void onOpen(
            IInAppMessage inAppMessage,
            boolean showOnlyIfLoaded,
            FragmentManager fragmentManager,
            int containerId,
            InAppMessageScreenActions screenActions
    ) {
        InAppMessageMainFragment inAppMessageFragment =
                new InAppMessageMainFragment();
        FragmentTransaction t = fragmentManager.beginTransaction()
                .add(
                        containerId,
                        inAppMessageFragment,
                        "IAM_MAIN_FRAGMENT"
                );
        t.addToBackStack("IAM_MAIN_FRAGMENT");
        t.commit();
    }
}
