package com.inappstory.sdk.stories.outercallbacks.common.objects;


import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageMainFragment;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageMainView;


public class DefaultOpenInAppMessageReader implements IOpenInAppMessageReader {
    public final static String IN_APP_MESSAGE_FRAGMENT = "IAM_MAIN_FRAGMENT";

    @Override
    public void onOpenInFragment(
            FragmentManager fragmentManager,
            int containerId
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
            t.commitNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpenInLayout(
            FrameLayout frameLayout
    ) {
        try {
            InAppMessageMainView inAppMessageView =
                    new InAppMessageMainView(frameLayout.getContext());
            inAppMessageView.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    )
            );
            frameLayout.addView(inAppMessageView);
        } catch (Exception e) {
        }
    }
}
