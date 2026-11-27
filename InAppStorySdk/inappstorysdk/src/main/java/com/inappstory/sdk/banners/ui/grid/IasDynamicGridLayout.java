package com.inappstory.sdk.banners.ui.grid;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.inappstory.sdk.R;

import java.util.ArrayList;
import java.util.List;

public class IasDynamicGridLayout extends ViewGroup {

    public static final int ALIGN_TOP = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_BOTTOM = 2;

    private int columnCount = 2;
    private int horizontalSpacing;
    private int verticalSpacing;
    private int rowAlignment = ALIGN_TOP;

    private final List<View> visibleChildren = new ArrayList<>();

    public IasDynamicGridLayout(Context context) {
        super(context);
        init(context, null);
    }

    public IasDynamicGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public IasDynamicGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        float density = context.getResources().getDisplayMetrics().density;
        horizontalSpacing = (int) (8 * density);
        verticalSpacing = (int) (8 * density);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IasDynamicGridLayout);
            columnCount = Math.max(1, a.getInt(R.styleable.IasDynamicGridLayout_columnCount, 2));
            horizontalSpacing = a.getDimensionPixelSize(
                    R.styleable.IasDynamicGridLayout_horizontalSpacing, horizontalSpacing);
            verticalSpacing = a.getDimensionPixelSize(
                    R.styleable.IasDynamicGridLayout_verticalSpacing, verticalSpacing);
            rowAlignment = a.getInt(R.styleable.IasDynamicGridLayout_rowAlignment, ALIGN_TOP);
            a.recycle();
        }
        LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
        transition.setStartDelay(LayoutTransition.CHANGING, 200);
        transition.setDuration(300);

        setLayoutTransition(transition);
    }

    public boolean removeItemAt(int index) {
        if (index >= 0 && index < getChildCount()) {
            removeViewAt(index);
            return true;
        }
        return false;
    }

    public <T> void setItems(List<T> items, ItemBinder<T> binder) {
        if (items == null) {
            removeAllViews();
            return;
        }
        int newSize = items.size();
        int currentChildCount = getChildCount();
        if (currentChildCount > newSize) {
            removeViews(newSize, currentChildCount - newSize);
        }
        for (int i = 0; i < newSize; i++) {
            View child;
            if (i < getChildCount()) {
                child = getChildAt(i);
            } else {
                child = binder.createView(this, i);
                addView(child);
            }
            binder.bindView(child, items.get(i), i);
        }
    }

    public boolean isItemByIndexVisibleOnScreen(int index) {
        if (index < 0 && index > getChildCount()) {
            return false;
        }
        View child = getChildAt(index);
        if (child == null || child.getParent() != this || !child.isShown()) {
            return false;
        }
        Rect visibleRect = new Rect();
        return child.getGlobalVisibleRect(visibleRect)
                && visibleRect.width() > 0
                && visibleRect.height() > 0;
    }

    public int columnCount() {
        return columnCount;
    }

    public void rowAlignment(int alignment) {
        if (rowAlignmentChanged(alignment)) requestLayout();
    }

    private boolean rowAlignmentChanged(int alignment) {
        if (alignment < ALIGN_TOP || alignment > ALIGN_BOTTOM) {
            alignment = ALIGN_TOP;
        }
        if (this.rowAlignment != alignment) {
            this.rowAlignment = alignment;
            return true;
        }
        return false;
    }

    public void columnCount(int columnCount) {
        if (columnCountChanged(columnCount)) requestLayout();
    }

    private boolean columnCountChanged(int columnCount) {
        int validCount = Math.max(1, columnCount);
        if (this.columnCount != validCount) {
            this.columnCount = validCount;
            return true;
        }
        return false;
    }

    public void horizontalSpacing(int horizontalSpacing) {
        if (horizontalSpacingChanged(horizontalSpacing)) requestLayout();
    }

    private boolean horizontalSpacingChanged(int spacingPx) {
        if (this.horizontalSpacing != spacingPx) {
            this.horizontalSpacing = spacingPx;
            return true;
        }
        return false;
    }

    public void verticalSpacing(int verticalSpacing) {
        if (verticalSpacingChanged(verticalSpacing)) requestLayout();
    }


    private boolean verticalSpacingChanged(int spacingPx) {
        if (this.verticalSpacing != spacingPx) {
            this.verticalSpacing = spacingPx;
            return true;
        }
        return false;
    }

    public void settings(
            int alignment,
            int verticalSpacing,
            int horizontalSpacing,
            int columnCount
    ) {
        if (rowAlignmentChanged(alignment) |
                verticalSpacingChanged(verticalSpacing) |
                horizontalSpacingChanged(horizontalSpacing) |
                columnCountChanged(columnCount))
            requestLayout();
    }

    public int rowAlignment() {
        return rowAlignment;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = totalWidth - getPaddingLeft() - getPaddingRight();

        int totalHorizontalSpacing = horizontalSpacing * (columnCount - 1);
        int columnWidth = Math.max(0, (availableWidth - totalHorizontalSpacing) / columnCount);

        int childWidthSpec = MeasureSpec.makeMeasureSpec(columnWidth, MeasureSpec.EXACTLY);

        int totalHeight = getPaddingTop() + getPaddingBottom();
        int currentRowMaxHeight = 0;
        int visibleIndex = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            ViewGroup.LayoutParams lp = child.getLayoutParams();
            int childHeightSpec;
            if (lp != null && lp.height > 0) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }

            child.measure(childWidthSpec, childHeightSpec);

            int col = visibleIndex % columnCount;
            if (col == 0) {
                if (visibleIndex > 0) {
                    totalHeight += currentRowMaxHeight + verticalSpacing;
                }
                currentRowMaxHeight = child.getMeasuredHeight();
            } else {
                currentRowMaxHeight = Math.max(currentRowMaxHeight, child.getMeasuredHeight());
            }
            visibleIndex++;
        }

        if (visibleIndex > 0) {
            totalHeight += currentRowMaxHeight;
        }

        setMeasuredDimension(totalWidth, resolveSize(totalHeight, heightMeasureSpec));
    }

    public int getColumnWidth() {
        int width = getWidth() > 0 ? getWidth() : getMeasuredWidth();
        return calculateColumnWidth(width);
    }

    public int calculateColumnWidth(int containerWidthPx) {
        if (containerWidthPx <= 0) {
            return 0;
        }
        int availableWidth = containerWidthPx - getPaddingLeft() - getPaddingRight();
        int totalHorizontalSpacing = horizontalSpacing * (columnCount - 1);
        return Math.max(0, (availableWidth - totalHorizontalSpacing) / columnCount);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        visibleChildren.clear();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                visibleChildren.add(child);
            }
        }

        if (visibleChildren.isEmpty()) {
            return;
        }

        int availableWidth = (r - l) - getPaddingLeft() - getPaddingRight();
        int totalHorizontalSpacing = horizontalSpacing * (columnCount - 1);
        int columnWidth = Math.max(0, (availableWidth - totalHorizontalSpacing) / columnCount);

        int currentTop = getPaddingTop();
        int totalVisible = visibleChildren.size();

        for (int rowStart = 0; rowStart < totalVisible; rowStart += columnCount) {
            int rowEnd = Math.min(rowStart + columnCount, totalVisible);

            int rowMaxHeight = 0;
            for (int i = rowStart; i < rowEnd; i++) {
                rowMaxHeight = Math.max(rowMaxHeight, visibleChildren.get(i).getMeasuredHeight());
            }

            for (int i = rowStart; i < rowEnd; i++) {
                View child = visibleChildren.get(i);
                int col = i - rowStart;
                int childHeight = child.getMeasuredHeight();

                int verticalOffset = 0;
                if (rowAlignment == ALIGN_CENTER) {
                    verticalOffset = (rowMaxHeight - childHeight) / 2;
                } else if (rowAlignment == ALIGN_BOTTOM) {
                    verticalOffset = rowMaxHeight - childHeight;
                }

                int left = getPaddingLeft() + col * (columnWidth + horizontalSpacing);
                int top = currentTop + verticalOffset;

                child.layout(
                        left,
                        top,
                        left + child.getMeasuredWidth(),
                        top + childHeight
                );
            }
            currentTop += rowMaxHeight + verticalSpacing;
        }
    }
}
