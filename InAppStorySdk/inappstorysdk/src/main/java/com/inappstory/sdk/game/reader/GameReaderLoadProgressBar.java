package com.inappstory.sdk.game.reader;

import static android.widget.RelativeLayout.CENTER_IN_PARENT;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.ui.views.IGameReaderLoaderView;
import com.inappstory.sdk.stories.utils.Sizes;



public class GameReaderLoadProgressBar extends View implements IGameReaderLoaderView {

    private int currentFrame = 0;             // Allocate paint outside onDraw to avoid unnecessary object creation
    private boolean isIndeterminate = true;

    private int progress = 0;

    public GameReaderLoadProgressBar(Context context) {
        this(context, null);
    }

    private final int strokeWidthDP = 4;

    private final int sizeDP = 36;
    private final boolean rounded = false;

    private float STROKE_WIDTH = Sizes.dpToPxExt(strokeWidthDP, null);
    private float STROKE_SIZE_HALF = STROKE_WIDTH / 2;

    private static Paint COLOR_PAINT;
    private static Paint GRADIENT_PAINT;

    Paint getColorPaint(Resources resources) {
        if (COLOR_PAINT == null) {
            COLOR_PAINT = new Paint();
            COLOR_PAINT.setColor(resources.getColor(R.color.cs_loaderColor));
            COLOR_PAINT.setStyle(Paint.Style.STROKE);
            COLOR_PAINT.setStrokeWidth(STROKE_WIDTH);
            if (rounded)
                COLOR_PAINT.setStrokeCap(Paint.Cap.ROUND);
            COLOR_PAINT.setAntiAlias(true);
        }

        return COLOR_PAINT;
    }

    Paint getColorGradientPaint(Resources resources) {
        if (GRADIENT_PAINT == null) {
            GRADIENT_PAINT = new Paint();
            GRADIENT_PAINT.setColor(resources.getColor(R.color.cs_loaderColor));
            GRADIENT_PAINT.setStyle(Paint.Style.STROKE);
            GRADIENT_PAINT.setStrokeWidth(STROKE_WIDTH);
            if (rounded)
                GRADIENT_PAINT.setStrokeCap(Paint.Cap.ROUND);

            float size = Sizes.dpToPxExt(sizeDP, getContext()) / 2f;
            Shader sweepGradient = new SweepGradient(
                    size,
                    size,
                    gradientColors,
                    positions
            );
            float rotate = 200f;
            Matrix gradientMatrix = new Matrix();
            gradientMatrix.preRotate(rotate, size, size);
            sweepGradient.setLocalMatrix(gradientMatrix);
            GRADIENT_PAINT.setShader(sweepGradient);
            GRADIENT_PAINT.setAntiAlias(true);
        }

        return GRADIENT_PAINT;
    }

    public GameReaderLoadProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameReaderLoadProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSize();
    }

    private void initSize() {
        STROKE_WIDTH = Sizes.dpToPxExt(strokeWidthDP, getContext());
        STROKE_SIZE_HALF = STROKE_WIDTH / 2;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                Sizes.dpToPxExt(sizeDP, getContext()),
                Sizes.dpToPxExt(sizeDP, getContext())
        );
        lp.addRule(CENTER_IN_PARENT);
        setLayoutParams(lp);

    }

    private RectF arcRect;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (arcRect == null) {
            arcRect = new RectF(STROKE_SIZE_HALF, STROKE_SIZE_HALF,
                    canvas.getWidth() - STROKE_SIZE_HALF,
                    canvas.getHeight() - STROKE_SIZE_HALF);
        }
        if (isIndeterminate) {
            drawIndeterminate(canvas);
        } else {
            drawDeterminate(canvas);
        }
        invalidate();
    }

    private void drawDeterminate(Canvas canvas) {
        canvas.save();
        canvas.drawArc(
                arcRect,
                -90,
                360 * (progress / 100f),
                false,
                getColorPaint(getResources())
        );
        canvas.restore();
    }

    private void drawIndeterminate(Canvas canvas) {

        currentFrame++;
        currentFrame = currentFrame % 450;
        int currentState = currentFrame % 90;
        float angle = 360f * (currentFrame / 450f);
        float value = 0f;
        if (currentState < 12 || currentState > 78) {
            value = 0f;
        } else if (currentState > 33 && currentState < 57) {
            value = 1f;
        } else if (currentState >= 57) {
            value = (currentState - 78f) / 22f; //close
        } else {
            value = (currentState - 12f) / 22f; //open
        }
        // 7200 ms - вся анимация
        drawIndeterminateOutlineArc(canvas, 72f * (currentState % 90) / 90f, value);
    }

    int[] gradientColors = {Color.parseColor("#00FFFFFF"), Color.parseColor("#FFFFFF")};
    float[] positions = {0f, 1f};

    private void drawIndeterminateOutlineArc(Canvas canvas, float angle, float value) {
        canvas.save();
        canvas.drawArc(
                arcRect,
                value > 0 ? -144 + angle : -144 + angle + 288 * (1 + value),
                288 * Math.abs(value),
                false,
                getColorGradientPaint(getResources())
        );
        canvas.restore();
    }


    @Override
    public View getView(Context context) {
        return this;
    }

    @Override
    public void setProgress(int progress, int max) {
        this.progress = progress;
    }

    @Override
    public void setIndeterminate(boolean indeterminate) {
        this.isIndeterminate = indeterminate;
    }
}
