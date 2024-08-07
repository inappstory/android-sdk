package com.inappstory.sdk.core.ui.screens.inappmessagereader;

import android.content.Context;

import com.inappstory.sdk.core.ui.screens.IScreenHolder;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategyType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;

public class LaunchIAMScreenStrategy implements LaunchScreenStrategy {


    @Override
    public void launch(Context context, IOpenReader openReader, IScreenHolder screenHolder) {

    }

    @Override
    public LaunchScreenStrategyType getType() {
        return LaunchScreenStrategyType.IN_APP_MESSAGE;
    }
}
