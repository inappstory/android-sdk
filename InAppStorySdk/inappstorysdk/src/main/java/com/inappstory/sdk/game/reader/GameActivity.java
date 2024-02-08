package com.inappstory.sdk.game.reader;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.utils.FragmentAction;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;


public class GameActivity extends AppCompatActivity implements BaseGameReaderScreen {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int theme = getIntent().getIntExtra("themeId", R.style.StoriesSDKAppTheme_GameActivity);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cs_game_reader_layout);
        ScreensManager.getInstance().subscribeGameScreen(this);
        createGameContentFragment(
                savedInstanceState,
                (GameReaderLaunchData) getIntent().getSerializableExtra(GameReaderLaunchData.SERIALIZABLE_KEY)
        );
    }

    private void createGameContentFragment(
            Bundle savedInstanceState,
            GameReaderLaunchData gameReaderLaunchData
    ) {
        if (savedInstanceState == null) {
            try {
                Fragment fragment = new GameReaderContentFragment();
                Bundle args = new Bundle();
                args.putSerializable(
                        gameReaderLaunchData.getSerializableKey(),
                        gameReaderLaunchData
                );
                fragment.setArguments(args);
                FragmentManager fragmentManager = getGameReaderFragmentManager();
                FragmentTransaction t = fragmentManager.beginTransaction()
                        .replace(R.id.stories_fragments_layout, fragment, fragmentTag);
                t.addToBackStack(fragmentTag);
                t.commit();
            } catch (IllegalStateException e) {
                InAppStoryService.createExceptionLog(e);
                forceFinish();
            }
        }
    }

    private final String fragmentTag = "GAME_READER_CONTENT";

    private boolean useContentFragment(FragmentAction<GameReaderContentFragment> action) {
        if (action != null) {
            try {
                Fragment fragmentById = getGameReaderFragmentManager().findFragmentByTag(fragmentTag);
                if (fragmentById instanceof GameReaderContentFragment) {
                    action.invoke((GameReaderContentFragment) fragmentById);
                    return true;
                }
            } catch (IllegalStateException e) {

            }
            action.error();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!useContentFragment(
                new FragmentAction<GameReaderContentFragment>() {
                    @Override
                    public void invoke(GameReaderContentFragment fragment) {
                        fragment.onBackPressed();
                    }

                    @Override
                    public void error() {

                    }
                })
        ) {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {

        ScreensManager.getInstance().unsubscribeGameScreen(this);
        super.onDestroy();
    }

    @Override
    public void forceFinish() {
        finish();
    }

    @Override
    public void close() {
        useContentFragment(
                new FragmentAction<GameReaderContentFragment>() {
                    @Override
                    public void invoke(GameReaderContentFragment fragment) {
                        fragment.closeGame();
                    }

                    @Override
                    public void error() {

                    }
                }
        );
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void setShowGoodsCallback(ShowGoodsCallback callback) {

    }

    @Override
    public void permissionResult(
            final int requestCode,
            @NonNull String[] permissions,
            @NonNull final int[] grantResults
    ) {
        useContentFragment(new FragmentAction<GameReaderContentFragment>() {
            @Override
            public void invoke(GameReaderContentFragment fragment) {
                fragment.permissionResult(requestCode, grantResults);
            }

            @Override
            public void error() {

            }
        });
    }

    @Override
    public FragmentManager getGameReaderFragmentManager() {
        return getSupportFragmentManager();
    }
}
