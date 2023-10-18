package com.inappstory.sdk.stories.ui.widgets.readerscreen.generated;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.lrudiskcache.Utils;
import com.inappstory.sdk.stories.cache.Downloader;

import java.io.File;
import java.io.IOException;

public class GeneratedVideoView extends RelativeLayout implements TextureView.SurfaceTextureListener, GeneratedViewCallback {

    boolean isLoaded;
    boolean isMpPrepared;
    String url;
    MediaPlayer mp;
    Surface surface;
    boolean isGenerated;

    ImageView cover;
    TextureView tv;

    LruDiskCache cache = InAppStoryService.getInstance().getCommonCache();

    private void init(Context context) {
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tv = new TextureView(context);
        cover = new ImageView(context);
        tv.setLayoutParams(lp);
        cover.setElevation(8);
        cover.setLayoutParams(lp);
        cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(tv);
        addView(cover);
    }

    public GeneratedVideoView(Context context) {
        super(context);
        init(context);
    }

    public GeneratedVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void loadCover(String path) {
        if (path == null) {
            cover.setBackgroundColor(Color.BLACK);
            return;
        }
        File fl = null;
        if (cache.hasKey(path)) {
            try {
                fl = cache.getFullFile(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (fl == null || !fl.exists()) {
            ImageLoader.getInstance().displayImage(path,
                    -1, cover, InAppStoryService.getInstance().getCommonCache());
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(fl.getAbsolutePath(), options);
            cover.setImageBitmap(bitmap);
            onLoaded();
        }
    }


    String storyId = null;

    public void loadVideo(String path, String storyId) {
        if (this.url == null || !this.url.equals(path)) {
            file = null;
        }
        this.url = path;
        isLoaded = true;
        this.storyId = storyId;
        if (tv.isAvailable()) {
            prepareVideo(tv.getSurfaceTexture());
        }

        tv.setSurfaceTextureListener(this);
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
        this.surface = new Surface(t);
        if (mp == null)
            mp = new MediaPlayer();
        mp.setSurface(this.surface);

        try {
            if (storyId == null)
                storyId = Utils.hash(Downloader.deleteQueryArgumentsFromUrl(url, false));
            if (file == null)
                if (cache.hasKey(url)) {
                    file = cache.getFullFile(url);
                }
            if (file != null && file.exists()) {
                boolean fileIsNotLocked = file.renameTo(file);
                if (file.length() > 10 && fileIsNotLocked) {
                    mp.setDataSource(file.getAbsolutePath());
                } else {
                    mp.setDataSource(url);
                }
            } else {
                mp.setDataSource(url);
                //Downloader.downloadFileBackground(url, false, cache, null);
            }
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(final MediaPlayer mp) {
                    isMpPrepared = true;
                    mp.setLooping(true);
                    updateTextureViewSize(getWidth(), getHeight(), mp.getVideoWidth(), mp.getVideoHeight());
                    mp.setVolume(0, 0);
                    mp.start();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cover.setVisibility(GONE);
                        }
                    }, 500);
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

        tv.setTransform(matrix);
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

    public void soundOnOff(boolean soundOff) {
        if (mp == null) return;
        if (soundOff) {
            mp.setVolume(0, 0);
        } else {
            mp.setVolume(1f, 1f);
        }
    }

    public void pausePlay() {
        if (mp != null)
            mp.pause();
    }

    public void stopPlay() {
        if (mp != null) {
            mp.seekTo(0);
            mp.pause();
        }
    }

    public void changePlayState() {
        if (mp != null) {
            if (mp.isPlaying())
                mp.pause();
            else
                mp.start();
        }

    }

    @Override
    public void onLoaded() {
        isVideoLoaded = true;
    }

    @Override
    public boolean isLoaded() {
        return isVideoLoaded;
    }

    boolean isVideoLoaded;
}
