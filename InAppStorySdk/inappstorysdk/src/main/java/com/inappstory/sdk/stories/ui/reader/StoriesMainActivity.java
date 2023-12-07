package com.inappstory.sdk.stories.ui.reader;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;

public class StoriesMainActivity extends FragmentActivity implements IStoriesReaderScreen {
    @Override
    public void forceClose() {
        finish();
    }

    @Override
    public void close(CloseReader action, String cause) {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ias_reader_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(
                            R.id.main_fragments_layout,
                            new StoriesReaderContainerFragment(),
                            StoriesReaderContainerFragment.TAG
                    )
                    .commit();
        }
    }
}
