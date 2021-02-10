package com.inappstory.sdk.stories.ui.video;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileType;
import com.inappstory.sdk.stories.cache.StoryDownloader;
import com.inappstory.sdk.stories.utils.Sizes;

import java.io.File;
import java.io.IOException;

public class VideoPlayer extends TextureView implements TextureView.SurfaceTextureListener {

    private static String TAG = "VideoPlayer";

    /**
     * This flag determines that if current VideoPlayer object is first item of the list if it is first item of list
     */
    boolean isFirstListItem;

    boolean isLoaded;
    boolean isMpPrepared;
    String url;
    MediaPlayer mp;
    Surface surface;
    SurfaceTexture s;

    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void loadVideo(String path) {

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

        if (mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void prepareVideo(SurfaceTexture t) {

        this.surface = new Surface(t);
        mp = new MediaPlayer();
        mp.setSurface(this.surface);

        try {
            File file = Downloader.getTestVideo(getContext(), url, FileType.STORY_IMAGE, -293, Sizes.getScreenSize());
            if (file.exists()) {
                mp.setDataSource(file.getAbsolutePath());
            } else {
                mp.setDataSource(url);
                StoryDownloader.downloadTestVideo(url);
            }
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    isMpPrepared = true;
                    mp.setLooping(true);

                    //Log.e("video_sizes", mp.getVideoWidth() + " " + mp.getVideoHeight() + " " + getWidth() + " " + getHeight());
                    updateTextureViewSize(getWidth(), getHeight(), mp.getVideoWidth(), mp.getVideoHeight());
                    mp.setVolume(0, 0);
                    mp.start();
                }
            });
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateTextureViewSize(float viewWidth, float viewHeight, float mVideoWidth, float mVideoHeight) {
        float scaleX = 1.0f;
        float scaleY = 1.0f;

        if (mVideoWidth > viewWidth && mVideoHeight > viewHeight) {
            scaleX = mVideoWidth / viewWidth;
            scaleY = mVideoHeight / viewHeight;
        } else if (mVideoWidth < viewWidth && mVideoHeight < viewHeight) {
            scaleY = viewWidth / mVideoWidth;
            scaleX = viewHeight / mVideoHeight;
        } else if (viewWidth > mVideoWidth) {
            scaleY = (viewWidth / mVideoWidth) / (viewHeight / mVideoHeight);
        } else if (viewHeight > mVideoHeight) {
            scaleX = (viewHeight / mVideoHeight) / (viewWidth / mVideoWidth);
        }

        // Calculate pivot points, in our case crop from center
        float pivotPointX = viewWidth / 2;
        float pivotPointY = viewHeight / 2;

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);

        setTransform(matrix);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
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