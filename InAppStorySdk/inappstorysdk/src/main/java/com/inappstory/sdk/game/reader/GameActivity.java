package com.inappstory.sdk.game.reader;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.iasutilsconnector.UtilModulesHolder;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.ui.screens.gamereader.BaseGameScreen;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;
import com.inappstory.sdk.stories.ui.utils.FragmentAction;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;


public class GameActivity extends AppCompatActivity implements BaseGameScreen {


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        int theme = getIntent().getIntExtra("themeId", R.style.StoriesSDKAppTheme_GameActivity);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cs_game_reader_layout);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                GameReaderAppearanceSettings appearanceSettings = (GameReaderAppearanceSettings) getIntent()
                        .getSerializableExtra(GameReaderAppearanceSettings.SERIALIZABLE_KEY);
                if (appearanceSettings != null) {
                    setNavBarColor(appearanceSettings.navBarColor);
                    setStatusBarColor(appearanceSettings.statusBarColor);
                }
                core.screensManager().getGameScreenHolder().subscribeScreen(GameActivity.this);
                createGameContentFragment(
                        savedInstanceState,
                        (GameReaderLaunchData) getIntent()
                                .getSerializableExtra(GameReaderLaunchData.SERIALIZABLE_KEY)

                );
            }

            @Override
            public void error() {
                forceFinish();
            }
        });

    }

    private void setStatusBarColor(String color) {
        if (color == null) return;
        getWindow().setStatusBarColor(Color.parseColor(color));
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        double bright = ColorUtils.getColorBright(getWindow().getStatusBarColor());
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
                FragmentManager fragmentManager = getScreenFragmentManager();
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
                Fragment fragmentById = getScreenFragmentManager().findFragmentByTag(fragmentTag);
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
        if (manager != null) {
            UtilModulesHolder utilModulesHolder = manager.iasCore().externalUtilsAPI().getUtilsAPI();
            if (utilModulesHolder.getFilePicker().onBackPressed()) return;
        }
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
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager().getGameScreenHolder().unsubscribeScreen(GameActivity.this);
            }
        });

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
    public void pauseScreen() {

    }

    @Override
    public void resumeScreen() {

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
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.externalUtilsAPI().getUtilsAPI()
                        .getFilePicker().permissionResult(requestCode, permissions, grantResults);
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
    public FragmentManager getScreenFragmentManager() {
        return getSupportFragmentManager();
    }
}
