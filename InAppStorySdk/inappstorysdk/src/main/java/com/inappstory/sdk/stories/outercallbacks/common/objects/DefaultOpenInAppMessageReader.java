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
import com.inappstory.sdk.inappmessage.InAppMessageViewController;
import com.inappstory.sdk.inappmessage.domain.reader.IAMViewController;
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
            final InAppMessageScreenActions screenActions,
            final IAMViewController viewController
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
            if (viewController instanceof InAppMessageViewController) {
                inAppMessageFragment.setController((InAppMessageViewController) viewController);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpenInLayout(
            FrameLayout frameLayout,
            InAppMessageScreenActions screenActions,
            IAMViewController viewController
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
            if (viewController instanceof InAppMessageViewController)
                inAppMessageView.setController((InAppMessageViewController) viewController);
            frameLayout.addView(inAppMessageView);
        } catch (Exception e) {
        }
    }
}
