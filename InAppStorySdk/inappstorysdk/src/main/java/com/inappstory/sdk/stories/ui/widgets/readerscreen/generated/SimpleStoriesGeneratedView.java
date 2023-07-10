package com.inappstory.sdk.stories.ui.widgets.readerscreen.generated;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.util.Pair;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.slidestructure.Element;
import com.inappstory.sdk.stories.api.models.slidestructure.SlideStructure;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.SimpleStoriesView;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.StoriesViewManager;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.ViewAnimator;

import java.util.ArrayList;
import java.util.HashMap;

public class SimpleStoriesGeneratedView extends RelativeLayout implements SimpleStoriesView {
    public SimpleStoriesGeneratedView(Context context) {
        super(context);
        init();
    }

    public SimpleStoriesGeneratedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleStoriesGeneratedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        manager = new StoriesViewManager(getContext());
        manager.setStoriesView(this);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - lastTempTap < 300)
                    manager.storyClick(null);
            }
        });
    }

    public void checkGenerator(CheckGeneratorEvent event) {
        for (View v : temp2) {
            if (v instanceof GeneratedViewCallback && !((GeneratedViewCallback) v).isLoaded())
                return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearTemp();
                if (temp2 != null && temp2.size() > 0)
                    temp2.get(temp2.size() - 1).setVisibility(VISIBLE);
            }
        }, 100);
        animatorHashMap.clear();
        for (View v : temp2) {
            if (v instanceof GeneratedImageView) {
                animatorHashMap.put(v, new ViewAnimator().animate(v, ViewAnimator.SHAKE));
            }
        }
        if (manager != null)
            manager.storyLoaded(-1);
    }

    void clearTemp() {
        synchronized (temp) {
            for (View v : temp) {
                if (v != null) {
                    v.setVisibility(GONE);
                    removeView(v);
                }
            }
            temp.clear();
        }
    }

    @Override
    public void pauseVideo() {
        for (GeneratedVideoView videoView : tempVideos) {
            videoView.pausePlay();
        }
        for (ValueAnimator animator : animatorHashMap.values()) {
            if (animator != null) {
                if (!animator.isPaused())
                    animator.pause();
            } else
                animator.cancel();
        }
    }

    HashMap<View, ValueAnimator> animatorHashMap = new HashMap<>();

    @Override
    public void playVideo() {
        for (GeneratedVideoView videoView : tempVideos) {
            videoView.startPlay();
        }
        for (ValueAnimator animator : animatorHashMap.values()) {
            if (animator != null && animator.isRunning()) continue;
            animator.start();
        }
    }

    @Override
    public void restartVideo() {

    }

    @Override
    public void stopVideo() {
        for (GeneratedVideoView videoView : tempVideos) {
            videoView.stopPlay();
        }
        for (ValueAnimator animator : animatorHashMap.values()) {
            if (animator != null) animator.cancel();
        }
    }

    @Override
    public void swipeUp() {

    }

    @Override
    public void loadJsApiResponse(String result, String cb) {

    }

    @Override
    public void resumeVideo() {
        for (GeneratedVideoView videoView : tempVideos) {
            videoView.startPlay();
        }
        for (ValueAnimator animator : animatorHashMap.values()) {
            if (animator != null) {
                if (animator.isPaused())
                    animator.resume();
                else {
                    animator.start();
                }
            }
        }
    }

    @Override
    public void changeSoundStatus() {
        for (GeneratedVideoView videoView : tempVideos) {
            videoView.startPlay();
        }
    }

    @Override
    public void cancelDialog(String id) {

    }

    ArrayList<View> temp = new ArrayList<>();
    ArrayList<View> temp2 = new ArrayList<>();
    ArrayList<GeneratedVideoView> tempVideos = new ArrayList<>();

    public void initViews(SlideStructure slideStructure) {
        for (int i = 0; i < getChildCount(); i++) {
            temp.add(getChildAt(i));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (getChildAt(i) instanceof RelativeLayout) {
                    getChildAt(i).setElevation(getChildAt(i).getElevation() + 2);
                }
            }
        }
        temp2.clear();
        tempVideos.clear();

        if (slideStructure.elements == null) {
            RelativeLayout rl = new RelativeLayout(getContext());
            rl.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rl.setBackgroundColor(Color.BLACK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rl.setElevation(4);
            }
            addView(rl);
            checkGenerator(null);
            return;
        }

        int xSize = Sizes.getScreenSize().x;
        int ySize = getHeight();
        float ratio = 480f / 310;
        float ratio2 = 1f * ySize / xSize;
        int topPadding = 0;
        int leftPadding = 0;
        if (ratio2 > ratio) {
            xSize = (int) (1f * ySize / ratio);
            leftPadding = (xSize - Sizes.getScreenSize().x) / 2;
        } else {
            ySize = (int) (1f * xSize * ratio);
            topPadding = (ySize - getHeight()) / 2;
        }
        RelativeLayout rl = new RelativeLayout(getContext());
        rl.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rl.setPaddingRelative(-leftPadding, -topPadding, -leftPadding, -topPadding);
        rl.setVisibility(GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rl.setElevation(4);
        }
        RelativeLayout rl2 = new RelativeLayout(getContext());
        rl2.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rl2.setPaddingRelative(-leftPadding, -topPadding, -leftPadding, -topPadding);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rl.setElevation(2);
        }
        addView(rl2);
        addView(rl);
        boolean hasVideo = false;
        for (Element element : slideStructure.elements) {
            GeneratedView gv = ElementGenerator.generate(element, getContext(), ySize, xSize);
            if (gv != null) {
                temp2.add(gv.view);
                if (gv.view instanceof GeneratedVideoView) {
                    hasVideo = true;
                    // clearTemp();
                    gv.addView(rl2);
                    tempVideos.add((GeneratedVideoView) gv.view);
                } else {
                    gv.addView(rl);
                }
                ElementGenerator.loadContent(element, gv, new SimpleViewCallback() {
                            @Override
                            public void doAction(String data) {
                                manager.storyClick(data);
                            }
                        },
                        Integer.toString(manager.storyId));
            }
        }
        if (slideStructure.background != null) {
            if (slideStructure.background.gradient) {
                Pair<String, String> gradientPair = ColorParser.getGradientColor(slideStructure.background.color);
                GradientDrawable shape = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{ColorParser.getColor(gradientPair.first, false),
                                ColorParser.getColor(gradientPair.second, false)});
                if (hasVideo) {
                    rl2.setBackground(shape);
                } else {
                    rl.setBackground(shape);
                }
            } else {
                if (hasVideo) {
                    rl2.setBackgroundColor(ColorParser.getColor(slideStructure.background.color, false));
                } else {
                    rl.setBackgroundColor(ColorParser.getColor(slideStructure.background.color, false));
                }
            }
        } else {
            if (hasVideo) {
                rl2.setBackgroundColor(Color.BLACK);
            } else {
                rl.setBackgroundColor(Color.BLACK);
            }
        }
        checkGenerator(null);
        temp2.add(rl2);
        temp2.add(rl);
    }

    @Override
    public void sendDialog(String id, String data) {

    }

    @Override
    public void destroyView() {

    }

    float coordinate1;
    long lastTap;
    long lastTempTap;

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (((ReaderPager)getParentForAccessibility()).cubeAnimation) return false;
        if (!InAppStoryService.isConnected()) return true;
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                coordinate1 = motionEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        boolean c = super.dispatchTouchEvent(motionEvent);
        return c;
    }

    boolean touchSlider = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (((ReaderPager)getParentForAccessibility()).cubeAnimation) return false;
        boolean c = super.onInterceptTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            lastTempTap = System.currentTimeMillis();
            if (System.currentTimeMillis() - lastTap < 1500) {
                return false;
            }

            lastTap = System.currentTimeMillis();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            touchSlider = false;
            getParentForAccessibility().requestDisallowInterceptTouchEvent(false);
        }
        return c || touchSlider;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (((ReaderPager)getParentForAccessibility()).cubeAnimation) return false;
        boolean c = super.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return true;
            }
        }
        return c;
    }

    @Override
    public float getCoordinate() {
        return coordinate1;
    }

    @Override
    public void shareComplete(String stId, boolean success) {

    }

    @Override
    public void freezeUI() {

    }

    @Override
    public void setStoriesView(SimpleStoriesView storiesView) {

    }

    @Override
    public void checkIfClientIsSet() {

    }

    @Override
    public void goodsWidgetComplete(String widgetId) {

    }


    @Override
    public void screenshotShare() {

    }

    StoriesViewManager manager;

    @Override
    public StoriesViewManager getManager() {
        return manager;
    }
}
