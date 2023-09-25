package com.inappstory.sdk.stories.ui.reader;


import static com.inappstory.sdk.AppearanceManager.BOTTOM_END;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_LEFT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_RIGHT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_START;
import static com.inappstory.sdk.AppearanceManager.CS_READER_SETTINGS;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT;
import static com.inappstory.sdk.AppearanceManager.TOP_END;
import static com.inappstory.sdk.AppearanceManager.TOP_LEFT;
import static com.inappstory.sdk.AppearanceManager.TOP_RIGHT;
import static com.inappstory.sdk.AppearanceManager.TOP_START;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;

import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanel;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.List;


public class StoriesLoaderFragment extends Fragment {

    ButtonsPanel buttonsPanel;
    View aboveButtonsPanel;

    View blackBottom;
    View blackTop;
    View refresh;


    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setViews(view);
    }

    void setViews(View view) {
        if (InAppStoryService.getInstance() == null) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(
                getArguments().getInt("storyId"),
                Story.StoryType.valueOf(
                        getArguments().getString(
                                "storiesType",
                                Story.StoryType.COMMON.name()
                        )
                )
        );
        if (story == null) return;
        if (buttonsPanel != null) {
            buttonsPanel.setButtonsVisibility(readerSettings,
                    story.hasLike(), story.hasFavorite(), story.hasShare(), story.hasAudio());
            buttonsPanel.setButtonsStatus(story.getLike(), story.favorite ? 1 : 0);
            aboveButtonsPanel.setVisibility(buttonsPanel.getVisibility());
        }
        setOffsets(view);

    }

    private void setOffsets(View view) {
        if (!Sizes.isTablet()) {
            if (blackBottom != null) {
                Point screenSize = Sizes.getScreenSize(getContext());
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) blackBottom.getLayoutParams();
                float realProps = screenSize.y / ((float) screenSize.x);
                float sn = 1.85f;
                if (realProps > sn) {
                    lp.height = (int) (screenSize.y - screenSize.x * sn) / 2;
                    setCutout(view, lp.height);
                } else {
                    setCutout(view, 0);
                }
                blackBottom.setLayoutParams(lp);
                blackTop.setLayoutParams(lp);
            }
        }
    }

    private void setCutout(View view, int minusOffset) {
        if (Build.VERSION.SDK_INT >= 28) {
            if (getActivity() != null && getActivity().getWindow() != null &&
                    getActivity().getWindow().getDecorView() != null &&
                    getActivity().getWindow().getDecorView().getRootWindowInsets() != null) {
                DisplayCutout cutout = getActivity().getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                if (cutout != null) {
                    View view1 = view.findViewById(R.id.ias_timeline_container);
                    if (view1 != null) {
                        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) view1.getLayoutParams();
                        lp1.topMargin += Math.max(cutout.getSafeInsetTop() - minusOffset, 0);
                        view1.setLayoutParams(lp1);
                    }
                }
            }
        }
    }

    void bindViews(View view) {
        refresh = view.findViewById(R.id.ias_refresh_button);
        blackBottom = view.findViewById(R.id.ias_black_bottom);
        blackTop = view.findViewById(R.id.ias_black_top);
        buttonsPanel = view.findViewById(R.id.ias_buttons_panel);
    }

    LinearLayout linearLayout;

    View createFragmentView(ViewGroup root) {
        Context context = getContext();

        RelativeLayout res = new RelativeLayout(context);
        res.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        if (!Sizes.isTablet() && readerSettings.backgroundColor != Color.BLACK) {
            linearLayout.setBackgroundColor(Color.BLACK);
        }
        setLinearContainer(context, linearLayout);
        res.addView(linearLayout);
        View emptyView = new View(context);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setClickable(true);
        res.addView(emptyView);
        return res;
    }

    private void setLinearContainer(Context context, LinearLayout linearLayout) {
        blackTop = new View(context);
        blackTop.setId(R.id.ias_black_top);
        blackTop.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        blackTop.setBackgroundColor(Color.TRANSPARENT);
        blackBottom = new View(context);
        blackBottom.setId(R.id.ias_black_bottom);
        blackBottom.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        blackBottom.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout content = new RelativeLayout(context);
        content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        ViewGroup main;
        RelativeLayout.LayoutParams contentLP = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        contentLP.addRule(RelativeLayout.ABOVE, R.id.ias_buttons_panel);
        aboveButtonsPanel = new View(context);
        aboveButtonsPanel.setBackgroundColor(Color.BLACK);
        aboveButtonsPanel.setVisibility(View.GONE);
        RelativeLayout.LayoutParams aboveLp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                Sizes.dpToPxExt(readerSettings.radius, context));
        aboveLp.addRule(RelativeLayout.ABOVE, R.id.ias_buttons_panel);
        aboveButtonsPanel.setLayoutParams(aboveLp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            main = new CardView(context);
            main.setLayoutParams(contentLP);
            ((CardView) main).setRadius(Sizes.dpToPxExt(readerSettings.radius, getContext()));
            ((CardView) main).setCardBackgroundColor(Color.BLACK);
            main.setElevation(0);

            RelativeLayout cardContent = new RelativeLayout(context);
            cardContent.setLayoutParams(new CardView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    CardView.LayoutParams.MATCH_PARENT));
           // cardContent.addView(createReaderContainer(context));
            //cardContent.addView(createTimelineContainer(context));
            main.addView(cardContent);
        } else {
            main = new RelativeLayout(context);
            main.setLayoutParams(contentLP);
            main.setBackgroundColor(Color.BLACK);
         //   main.addView(createReaderContainer(context));
          //  main.addView(createTimelineContainer(context));
        }
        createButtonsPanel(context);
        content.addView(buttonsPanel);
        content.addView(aboveButtonsPanel);
        content.addView(main);
        linearLayout.addView(blackTop);
        linearLayout.addView(content);
        linearLayout.addView(blackBottom);
    }

    private void createButtonsPanel(Context context) {
        buttonsPanel = new ButtonsPanel(context);
        RelativeLayout.LayoutParams buttonsPanelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, Sizes.dpToPxExt(60, context)
        );
        buttonsPanelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        buttonsPanel.setVisibility(View.GONE);
        buttonsPanel.setId(R.id.ias_buttons_panel);
        buttonsPanel.setOrientation(LinearLayout.HORIZONTAL);
        buttonsPanel.setBackgroundColor(Color.BLACK);
        buttonsPanel.setLayoutParams(buttonsPanelParams);
        buttonsPanel.setIcons(readerSettings);
    }


    private RelativeLayout createReaderContainer(Context context) {
        RelativeLayout readerContainer = new RelativeLayout(context);

        readerContainer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            readerContainer.setElevation(9);
        }
        if (readerSettings.timerGradientEnable)
            addGradient(context, readerContainer);

        createLoader();
        loaderContainer = new RelativeLayout(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loaderContainer.setElevation(28);
        }
        loaderContainer.setLayoutParams(
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        loaderContainer.setBackgroundColor(Color.BLACK);
        loaderContainer.addView(loader);
        readerContainer.addView(loaderContainer);
        return readerContainer;
    }

    View loader;
    RelativeLayout loaderContainer;

    private void createLoader() {
        Context context = getContext();
        loader = new RelativeLayout(context);
        loader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loader.setElevation(8);
        }
        ((ViewGroup) loader).addView(AppearanceManager.getLoader(context));
    }

    private void addGradient(Context context, RelativeLayout relativeLayout) {
        View gradientView = new View(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gradientView.setElevation(8);
            gradientView.setOutlineProvider(null);
        }
        gradientView.setClickable(false);
        if (timerGradient != null) {
            List<Integer> colors = timerGradient.csColors;
            List<Float> locations = timerGradient.csLocations;
            final int[] colorsArray = new int[timerGradient.csColors.size()];
            final float[] locationsArray = new float[timerGradient.csColors.size()];

            if (colors == null ||
                    colors.isEmpty()) {
                return;
            }
            if (colors.size() != locations.size()) return;
            int i = 0;
            for (Integer color : colors) {
                colorsArray[i] = color.intValue();
                i++;
            }
            i = 0;
            for (Float location : locations) {
                locationsArray[i] = location.floatValue();
                i++;
            }
            if (timerGradient.csGradientHeight > 0) {
                lp.height = Sizes.dpToPxExt(timerGradient.csGradientHeight, context);
            }
            ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
                @Override
                public Shader resize(int width, int height) {

                    return new LinearGradient(0f, 0f, 0f, 1f * height,
                            colorsArray,
                            locationsArray,
                            Shader.TileMode.REPEAT);
                }
            };
            PaintDrawable paint = new PaintDrawable();
            paint.setShape(new RectShape());
            paint.setShaderFactory(shaderFactory);
            gradientView.setBackground(paint);
        } else {
            gradientView.setBackground(getResources().getDrawable(R.drawable.story_gradient));
        }

        gradientView.setLayoutParams(lp);

        relativeLayout.addView(gradientView);
    }

    StoriesReaderSettings readerSettings = null;
    StoriesGradientObject timerGradient = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        readerSettings = JsonParser.fromJson(
                getArguments().getString(CS_READER_SETTINGS),
                StoriesReaderSettings.class
        );
        timerGradient = (StoriesGradientObject) getArguments().getSerializable(CS_TIMER_GRADIENT);
        try {
            return createFragmentView(container);
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            return new View(getContext());
        }
    }


}
