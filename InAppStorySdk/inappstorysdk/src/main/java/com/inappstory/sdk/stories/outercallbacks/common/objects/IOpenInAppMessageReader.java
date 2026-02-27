package com.inappstory.sdk.stories.outercallbacks.common.objects;

import android.content.Context;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.BaseIAMScreen;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.domain.reader.IAMViewController;

public interface IOpenInAppMessageReader extends IOpenReader {
    void onOpenInFragment(
            FragmentManager fragmentManager,
            int containerId,
            InAppMessageScreenActions screenActions,
            IAMViewController viewController
    );

    BaseIAMScreen onOpenInLayout(
            Context context,
            InAppMessageScreenActions screenActions
    );
}
