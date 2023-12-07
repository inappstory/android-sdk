package com.inappstory.sdk.stories.ui.reader;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.inappstory.sdk.R;
import com.inappstory.sdk.databinding.IasReaderMainBinding;
import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;
import com.inappstory.sdk.stories.utils.StatusBarController;

public class StoriesMainActivity extends FragmentActivity implements IStoriesReaderScreen {
    @Override
    public void forceClose() {
        StatusBarController.showStatusBar(this);
        finish();
    }

    @Override
    public void close(CloseReader action, String cause) {
        forceClose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IasReaderMainBinding mainBinding = IasReaderMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        StatusBarController.hideStatusBar(this, true);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(
                            mainBinding.mainFragmentsLayout.getId(),
                            new StoriesReaderContainerFragment(),
                            StoriesReaderContainerFragment.TAG
                    )
                    .commit();
        }
    }
}
