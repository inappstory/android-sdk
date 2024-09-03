package com.inappstory.sdk.game.reader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.ui.screens.GetScreenCallback;
import com.inappstory.sdk.core.ui.screens.gamereader.BaseGameScreen;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;
import com.inappstory.sdk.stories.ui.utils.FragmentAction;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

public class GameMainFragment extends Fragment
        implements BaseGameScreen, IASBackPressHandler {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.cs_game_reader_layout, container, false);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.getScreensHolder().getStoryScreenHolder()
                        .useCurrentReader(new GetScreenCallback<BaseStoryScreen>() {
                            @Override
                            public void get(BaseStoryScreen screen) {
                                screen.pauseScreen();
                            }
                        });
            }
        });
    }

    @Override
    public void onDestroy() {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.getScreensHolder().getStoryScreenHolder()
                        .useCurrentReader(
                                new GetScreenCallback<BaseStoryScreen>() {
                                    @Override
                                    public void get(BaseStoryScreen screen) {
                                        screen.resumeScreen();
                                    }
                                }
                        );
            }
        });
        super.onDestroy();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) {
            forceFinish();
            return;
        }

        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.getScreensHolder().getGameScreenHolder().subscribeScreen(GameMainFragment.this);
            }
        });
        createGameContentFragment(
                savedInstanceState,
                (GameReaderLaunchData) getArguments().getSerializable(GameReaderLaunchData.SERIALIZABLE_KEY)
        );
    }

    @Override
    public void onDestroyView() {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.getScreensHolder().getGameScreenHolder().unsubscribeScreen(
                        GameMainFragment.this
                );
            }
        });
        super.onDestroyView();
    }


    @Override
    public void forceFinish() {
        getParentFragmentManager().popBackStack();
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
    public void setShowGoodsCallback(ShowGoodsCallback callback) {

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
    public void permissionResult(
            final int requestCode,
            @NonNull final String[] permissions,
            @NonNull final int[] grantResults
    ) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
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
    public FragmentManager getScreenFragmentManager() {

        return getChildFragmentManager();
    }

    @Override
    public boolean onBackPressed() {
        return useContentFragment(
                new FragmentAction<GameReaderContentFragment>() {
                    @Override
                    public void invoke(GameReaderContentFragment fragment) {
                        fragment.onBackPressed();
                    }

                    @Override
                    public void error() {

                    }
                }
        );
    }
}
