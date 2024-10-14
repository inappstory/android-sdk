package com.inappstory.sdk.stories.ui.video;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.InAppStoryService;

import java.io.File;

public class VideoPlayer extends TextureView implements TextureView.SurfaceTextureListener {

    boolean isLoaded;
    boolean isMpPrepared;
    String url;
    MediaPlayer mp;
    Surface surface;

    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void loadVideo(String path) {
        file = new File(path);
        isLoaded = true;

        if (this.isAvailable()) {
            prepareVideo(getSurfaceTexture());
        }

        setSurfaceTextureListener(this);
    }

    public void loadVideoByUrl(String path) {
        if (this.url == null || !this.url.equals(path)) {
            file = null;
        }
        this.url = path;
        isLoaded = true;

        if (this.isAvailable()) {
            prepareVideo(getSurfaceTexture());
        }

        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {
        isMpPrepared = false;
        prepareVideo(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (this.surface != null) this.surface.release();
        if (mp != null) {
            // this.surface.release();
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }

        return false;
    }

    public void destroy() {
        if (this.surface != null) this.surface.release();
        this.surface = null;
        if (mp != null) {
            // this.surface.release();
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    }

    public void release() {
        if (mp != null) {
            // this.surface.release();
            //surface.release();

            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    File file = null;

    public void prepareVideo(SurfaceTexture t) {
        if (t == null) return;
        if (parent != null && parent.getScrollState() != SCROLL_STATE_IDLE) return;
        this.surface = new Surface(t);
        if (mp == null)
            mp = new MediaPlayer();
        mp.setSurface(this.surface);
        try {
            if (file != null && file.exists()) {
                boolean fileIsNotLocked = file.renameTo(file);
                if (file.length() > 10 && fileIsNotLocked) {
                    mp.setDataSource(file.getAbsolutePath());
                    mp.prepareAsync();
                }
            } else {
                return;
            }
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    isMpPrepared = true;
                    mp.setLooping(true);
                    updateTextureViewSize(getWidth(), getHeight(), mp.getVideoWidth(), mp.getVideoHeight());
                    mp.setVolume(0, 0);
                    mp.start();
                }
            });
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            e.printStackTrace();
        }

    }

    private void updateTextureViewSize(float viewWidth, float viewHeight, float mVideoWidth, float mVideoHeight) {
        float scaleX = 1.0f;
        float scaleY = 1.0f;
        float coeff1 = viewWidth / viewHeight;
        float coeff2 = mVideoWidth / mVideoHeight;

        if (mVideoWidth / viewWidth > mVideoHeight / viewHeight) {
            scaleX = coeff2 / coeff1;
        } else {
            scaleY = coeff1 / coeff2;
        }

        float pivotPointX = viewWidth / 2;
        float pivotPointY = viewHeight / 2;

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);

        setTransform(matrix);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (parent != null) {
            parent.removeOnScrollListener(scrollListener);
        }
        destroy();
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (lastState == SCROLL_STATE_IDLE && newState != SCROLL_STATE_IDLE) {
                if (mp != null) mp.pause();
                //  destroy();
            } else if (newState == SCROLL_STATE_IDLE && lastState != SCROLL_STATE_IDLE) {
                if (mp != null) mp.start();
                else prepareVideo(getSurfaceTexture());
            }
            lastState = newState;
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        parent = getParentRecyclerView(this);
        if (parent != null)
            parent.addOnScrollListener(scrollListener);
        prepareVideo(getSurfaceTexture());

    }

    RecyclerView parent;
    int lastState = SCROLL_STATE_IDLE;

    private RecyclerView getParentRecyclerView(View view) {
        ViewParent viewParent = view.getParentForAccessibility();
        if (viewParent instanceof RecyclerView) return (RecyclerView) viewParent;
        if (viewParent instanceof View) {
            return getParentRecyclerView((View) viewParent);
        }
        return null;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    public boolean startPlay() {
        if (mp != null)
            if (!mp.isPlaying()) {
                mp.start();
                return true;
            }

        return false;
    }

    public void pausePlay() {
        if (mp != null)
            mp.pause();
    }

    public void stopPlay() {
        if (mp != null)
            mp.stop();
    }

    public void changePlayState() {
        if (mp != null) {
            if (mp.isPlaying())
                mp.pause();
            else
                mp.start();
        }

    }
}