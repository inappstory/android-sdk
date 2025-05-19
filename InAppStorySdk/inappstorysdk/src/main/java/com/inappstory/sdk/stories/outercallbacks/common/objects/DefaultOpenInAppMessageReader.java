package com.inappstory.sdk.stories.outercallbacks.common.objects;


import android.os.Bundle;
import android.util.Log;

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
            Bundle args = new Bundle();
            args.putInt("iamId", inAppMessage.id());
            inAppMessageFragment.setArguments(args);
            FragmentTransaction t = fragmentManager.beginTransaction()
                    .add(
                            containerId,
                            inAppMessageFragment,
                            IN_APP_MESSAGE_FRAGMENT
                    );
            //   t.addToBackStack(IN_APP_MESSAGE_FRAGMENT);
            t.commit();

            Log.e("ShowIAMLog", System.currentTimeMillis() + "_finish");
        } catch (Exception e) {

        }
    }
}
