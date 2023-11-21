package com.inappstory.sdk.game.reader;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;


public class GameActivity extends AppCompatActivity implements BaseGameReaderScreen {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cs_game_reader_layout);
        if (savedInstanceState == null) {
            try {
                Fragment fragment = new GameReaderContentFragment();
                Bundle args = new Bundle();
                args.putSerializable(
                        GameReaderLaunchData.SERIALIZABLE_KEY,
                        getIntent().getSerializableExtra(GameReaderLaunchData.SERIALIZABLE_KEY)
                );
                fragment.setArguments(args);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction t = fragmentManager.beginTransaction()
                        .replace(R.id.fragments_layout, fragment, "GAME_READER_CONTENT");
                t.addToBackStack("GAME_READER_CONTENT");
                t.commit();
            } catch (IllegalStateException e) {
                InAppStoryService.createExceptionLog(e);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void closeGameReader(int action) {

    }

    @Override
    public void forceFinish() {

    }

    @Override
    public void shareComplete(String shareId, boolean shared) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void setShowGoodsCallback(ShowGoodsCallback callback) {

    }

    @Override
    public void permissionResult() {

    }

    @Override
    public FragmentManager getGameReaderFragmentManager() {
        return null;
    }
}
