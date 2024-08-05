package com.inappstory.sdk.game.reader;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderAppearanceSettings;
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
        GameReaderAppearanceSettings appearanceSettings = (GameReaderAppearanceSettings) getIntent()
                .getSerializableExtra(GameReaderAppearanceSettings.SERIALIZABLE_KEY);
        if (appearanceSettings != null) {
            setNavBarColor(appearanceSettings.navBarColor);
            setStatusBarColor(appearanceSettings.statusBarColor);
        }

        ScreensManager.getInstance().subscribeGameScreen(this);
        createGameContentFragment(
                savedInstanceState,
                (GameReaderLaunchData) getIntent()
                        .getSerializableExtra(GameReaderLaunchData.SERIALIZABLE_KEY)

        );
    }

    private void setStatusBarColor(String color) {
        if (color == null) return;
        getWindow().setStatusBarColor(Color.parseColor(color));
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        int rgb = getWindow().getStatusBarColor();   // convert rrggbb to decimal
        int r = (rgb >> 16) & 0xff;  // extract red
        int g = (rgb >> 8) & 0xff;  // extract green
        int b = (rgb) & 0xff;  // extract blue

        float bright = 0.2126f * r + 0.7152f * g + 0.0722f * b;
        windowInsetsController.setAppearanceLightStatusBars(bright > 40);
        /*if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO)
            windowInsetsController.setAppearanceLightStatusBars(true);
        else
            windowInsetsController.setAppearanceLightStatusBars(false);*/
    }


    private void setNavBarColor(String color) {
        if (color == null) return;
        getWindow().setNavigationBarColor(Color.parseColor(color));
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
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager != null && manager.utilModulesHolder != null && manager.utilModulesHolder.getFilePicker().onBackPressed())
            return;
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
            @NonNull final String[] permissions,
            @NonNull final int[] grantResults
    ) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                if (manager.utilModulesHolder != null)
                    manager.utilModulesHolder.getFilePicker().permissionResult(requestCode, permissions, grantResults);
            }
        });
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
