package com.inappstory.sdk.stories.ui.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.utils.Sizes;


public class CoreProgressBar extends View {

    private static final int FRAME_DURATION = 50; // ms


    private float STROKE_WIDTH = Sizes.dpToPxExt(4, null);
    private float STROKE_SIZE_HALF = STROKE_WIDTH / 2;


    public int getProgress() {
        return progress;
    }

    /**
     * текущий прогресс
     */
    private int progress;
    /**
     * не используется, взято из старого приложения
     */
    private int color;
    /**
     * значение текущего кадра (отвечает за угол поворота лоадера)
     */
    private int currentFrame;
    /**
     * время, когда был показан последний кадр
     */
    private long lastFrameShown;
    /**
     * не используется, взято из старого приложения
     */
    private RectF arcRect;

    public CoreProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public CoreProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CoreProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    @SuppressWarnings("unused")
    public CoreProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }


    /**
     * шрифт в загрузчике
     */
    Typeface tf;

    /**
     * инициализация элемента
     *
     * @param con контекст (не используется)
     * @param attrs параметры элемента из xml (не используется)
     */
    private void init(Context con, AttributeSet attrs) {

        tf = Typeface.DEFAULT;
        STROKE_WIDTH = Sizes.dpToPxExt(4, con);
        STROKE_SIZE_HALF = STROKE_WIDTH / 2;
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IndexOutOfBoundsException("progress must be 0..100");
        }
        this.progress = progress;
    }

    private static Paint COLOR_PAINT;

    private Paint getColorPaint(Resources resources) {
        if (COLOR_PAINT == null) {
            COLOR_PAINT = new Paint();
            COLOR_PAINT.setColor(resources.getColor(R.color.cs_loaderColor));
            COLOR_PAINT.setStyle(Paint.Style.STROKE);
            COLOR_PAINT.setStrokeWidth(STROKE_WIDTH);
            COLOR_PAINT.setAntiAlias(true);
        }

        return COLOR_PAINT;
    }

    /**
     * перед отрисовкой выравниваем местоположение битмапа и устанавливаем нужный угол
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (arcRect == null) {
            arcRect = new RectF(STROKE_SIZE_HALF, STROKE_SIZE_HALF,
                    canvas.getWidth() - STROKE_SIZE_HALF,
                    canvas.getHeight() - STROKE_SIZE_HALF);
        }

        Paint paint = getColorPaint(getResources());
        paint.setAntiAlias(true);

        long now = SystemClock.uptimeMillis();
        if (now > lastFrameShown + FRAME_DURATION) {
            currentFrame++;
            lastFrameShown = now;
        }
        int curAngle = currentFrame % 24;
        Matrix matrix = new Matrix();

        paint.setColor(getContext().getResources().getColor(R.color.cs_loaderColor));
        paint.setTypeface(tf);
        paint.setTextSize(Sizes.dpToPxExt(12, getContext()));
        paint.setTextAlign(Paint.Align.CENTER);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        drawProgress(canvas, paint);

        invalidate();
    }

    private void drawProgress(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.rotate(-90, canvas.getWidth() / 2, canvas.getHeight() / 2);
        int deg = (int) (3.6f * progress);
        canvas.drawArc(arcRect, 0, deg, false, paint);
        canvas.restore();
    }
}

