package com.inappstory.sdk.stories.outercallbacks.common.objects;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessageBottomSheetSettings;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessageFullscreenSettings;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessageModalSettings;
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
        Bundle arguments = new Bundle();
        arguments.putInt("id", inAppMessage.id());
        arguments.putInt("type", inAppMessage.screenType());
        arguments.putBoolean("showOnlyIfLoaded", showOnlyIfLoaded);

        switch (inAppMessage.screenType()) {
            case 1:
                arguments.putSerializable(
                        "screenParameters",
                        new InAppMessageBottomSheetSettings()
                );
                break;
            case 2:
                arguments.putSerializable(
                        "screenParameters",
                        new InAppMessageModalSettings()
                );
                break;
            case 3:
                arguments.putSerializable(
                        "screenParameters",
                        new InAppMessageFullscreenSettings()
                );
                break;
            default:
                break;
        }
        inAppMessageFragment.setArguments(arguments);
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
