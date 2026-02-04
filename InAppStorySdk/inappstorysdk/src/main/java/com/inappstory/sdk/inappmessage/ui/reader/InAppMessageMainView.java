package com.inappstory.sdk.inappmessage.ui.reader;

import static com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer.CONTAINER_ID;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.CancellationTokenWithStatus;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.BaseIAMScreen;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderLoadStates;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderLoaderStates;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderState;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderUIStates;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;
import com.inappstory.sdk.inappmessage.InAppMessageViewController;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBottomSheetAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageFullscreenAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessagePopupAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageToastAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessageUndefinedSettings;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContainerCallback;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

import java.util.Objects;

public class InAppMessageMainView extends FrameLayout implements Observer<IAMReaderState>, BaseIAMScreen {
    private IAMReaderLoadStates currentLoadState = IAMReaderLoadStates.EMPTY;
    private IAMReaderLoaderStates currentLoaderState = IAMReaderLoaderStates.EMPTY;
    private IAMReaderUIStates currentUIState = IAMReaderUIStates.CLOSED;
    private IIAMReaderViewModel readerViewModel;
    private boolean contentIsPreloaded;
    private InAppMessageAppearance appearance = new InAppMessageUndefinedSettings();
    private IAMContentContainer contentContainer;
    InAppMessageViewController controller;

    public void setController(InAppMessageViewController controller) {
        this.controller = controller;
    }

    public InAppMessageMainView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public InAppMessageMainView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InAppMessageMainView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    @Override
    protected void onDetachedFromWindow() {
        if (controller != null) {
            controller.unsubscribeView(this);
        }
        if (readerViewModel != null) {
            readerViewModel.removeSubscriber(InAppMessageMainView.this);
            readerViewModel.clear();
        } else {
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.screensManager().iamReaderViewModel()
                            .removeSubscriber(InAppMessageMainView.this);
                }
            });
        }
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager().getIAMScreenHolder()
                        .unsubscribeScreen(InAppMessageMainView.this);
            }
        });
        super.onDetachedFromWindow();
        try {
            if (onCloseAction != null) onCloseAction.onClose();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (controller != null) {
            controller.subscribeView(this);
        }
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (readerViewModel == null || manager == null) return;
        if (!contentIsPreloaded) {
            readerViewModel.updateCurrentUiState(IAMReaderUIStates.OPENING);
            readerViewModel.updateCurrentLoaderState(IAMReaderLoaderStates.LOADING);
        }
        IASCore core = manager.iasCore();
        core.screensManager()
                .getIAMScreenHolder().subscribeScreen(InAppMessageMainView.this);
        String tokenId = readerViewModel.getCurrentState().cancellationTokenUID;
        if (tokenId != null) {
            CancellationTokenWithStatus token = core.cancellationTokenPool().getTokenByUID(tokenId);
            if (token != null) {
                if (token.cancelled())
                    forceFinish();
                else
                    token.disable();
            }
        }
        if (contentContainer != null) {
            contentView = new IAMContentLayout(getContext());
            contentView.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    )
            );
            contentContainer.addViewToContent(contentView);
            contentContainer.setRefreshClick(v -> {
                if (contentView != null) {
                    contentView.refreshClick();
                }
            });
        }
    }


    public void setOnOpenAction(InAppMessageOpenAction onOpenAction) {
        this.onOpenAction = onOpenAction;
    }

    public void setOnCloseAction(InAppMessageCloseAction onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    private InAppMessageCloseAction onCloseAction;
    private InAppMessageOpenAction onOpenAction;


    private void init(Context context) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                readerViewModel = core.screensManager().iamReaderViewModel();
                readerViewModel.addSubscriber(InAppMessageMainView.this);
                IAMReaderState state = readerViewModel.getCurrentState();
                contentIsPreloaded = state.contentIsPreloaded;
                appearance = state.appearance;
            }
        });
        int layoutId = R.layout.cs_inappmessage_fullscreen_layout;
        if (appearance instanceof InAppMessagePopupAppearance) {
            layoutId = R.layout.cs_inappmessage_popup_layout;
        } else if (appearance instanceof InAppMessageFullscreenAppearance) {
            layoutId = R.layout.cs_inappmessage_fullscreen_layout;
        } else if (appearance instanceof InAppMessageToastAppearance) {
            layoutId = R.layout.cs_inappmessage_toast_layout;
        } else if (appearance instanceof InAppMessageBottomSheetAppearance) {
            layoutId = R.layout.cs_inappmessage_bottomsheet_layout;
        }
        inflate(context, layoutId, this);
        contentContainer = findViewById(CONTAINER_ID);
        contentContainer.setVisibility(View.INVISIBLE);
        if (appearance != null) {
            contentContainer.appearance(appearance);
        }
        contentContainer.uiContainerCallback(containerCallback);
    }

    IAMContainerCallback containerCallback = new IAMContainerCallback() {
        @Override
        public void countSafeArea(Pair<Integer, Integer> safeArea) {
            readerViewModel.updateCurrentSafeArea(safeArea);
        }

        @Override
        public void onShown() {
            readerViewModel.updateCurrentUiState(IAMReaderUIStates.OPENED);

        }

        @Override
        public void onClosed() {
            readerViewModel.updateCurrentUiState(IAMReaderUIStates.CLOSED);
        }
    };

    public void showContainer() {
        if (!contentIsPreloaded)
            contentContainer.showLoader();
        contentContainer.showWithAnimation();
    }


    public void hideContainerWithAnimation() {
        contentContainer.closeWithAnimation();
    }

    public void hideContainer() {
        try {
            ((FrameLayout)getParent()).removeView(this);
        } catch (Exception ignored) {
        }
    }

    IAMContentLayout contentView;

    @Override
    public void onUpdate(final IAMReaderState newValue) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (newValue.uiState != currentUIState) {
                    uiStateIsChanged(newValue.uiState);
                }
                if (newValue.loadState != currentLoadState) {
                    loadStateIsChanged(newValue.loadState);
                }
                if (newValue.loaderState != currentLoaderState) {
                    loaderStateChanged(newValue.loaderState);
                }
            }
        });

    }

    private void loaderStateChanged(IAMReaderLoaderStates state) {
        currentLoaderState = state;
        switch (state) {
            case EMPTY:
                break;
            case FAILED:
                contentContainer.showRefresh();
                break;
            case LOADED:
                contentContainer.hideLoader();
                contentContainer.hideRefresh();
                break;
            case LOADING:
                contentContainer.showLoader();
                break;
        }
    }

    private void uiStateIsChanged(IAMReaderUIStates uiState) {
        currentUIState = uiState;
        switch (uiState) {
            case OPENING:
                showContainer();
                break;
            case CLOSING:
                hideContainerWithAnimation();
                break;
            case CLOSED:
                hideContainer();
                break;
            case OPENED:
                if (onOpenAction != null)
                    onOpenAction.onOpen();
                break;
        }
    }


    private void loadStateIsChanged(IAMReaderLoadStates newState) {
        if (Objects.equals(currentLoadState, newState)) return;
        currentLoadState = newState;
        if (newState == null) return;
        switch (newState) {
            case ASSETS_LOADED:
            case CONTENT_LOADED:
                contentView.readerSlideViewModel.updateLayout();
                break;
            case ASSETS_FAILED:
            case CONTENT_FAILED:
            case CONTENT_LOADING:
            case ASSETS_LOADING:
                break;
        }
        if (Objects.requireNonNull(newState) == IAMReaderLoadStates.RENDER_READY) {
            if (currentUIState != IAMReaderUIStates.OPENED &&
                    currentUIState != IAMReaderUIStates.OPENING) {
                readerViewModel.updateCurrentUiState(IAMReaderUIStates.OPENING);
            }
        } else if (Objects.requireNonNull(newState) == IAMReaderLoadStates.CONTENT_FAILED_CLOSE) {
            readerViewModel.updateCurrentUiState(IAMReaderUIStates.CLOSING);
        }
    }

    @Override
    public void forceFinish() {
        contentContainer.closeWithoutAnimation();
    }

    @Override
    public void close() {
        contentContainer.closeWithAnimation();
    }

    @Override
    public void pauseScreen() {
        if (contentView != null) {
            contentView.onPause();
        }
    }

    @Override
    public void resumeScreen() {
        if (contentView != null) {
            contentView.onResume();
        }
    }

    @Override
    public void setShowGoodsCallback(ShowGoodsCallback callback) {

    }

    @Override
    public void permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public FragmentManager getScreenFragmentManager() {
        return null;
    }
}
