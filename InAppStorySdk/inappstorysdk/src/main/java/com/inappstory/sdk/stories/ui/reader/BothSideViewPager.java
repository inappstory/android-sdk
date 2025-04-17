package com.inappstory.sdk.stories.ui.reader;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.HashMap;

public class BothSideViewPager extends ViewPager {

    private final HashMap<OnPageChangeListener, SidesPageChangeListener> mPageChangeListeners = new HashMap<>();
    private int layoutDirection = ViewCompat.LAYOUT_DIRECTION_LTR;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("ViewPagerTouch", "onInterceptTouchEvent " + ev);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.e("ViewPagerTouch", "onTouchEvent " + ev);
        return super.onTouchEvent(ev);
    }

    public BothSideViewPager(Context context) {
        super(context);
    }

    public BothSideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onRtlPropertiesChanged(int direction) {
        super.onRtlPropertiesChanged(direction);
        int viewCompatLayoutDirection = direction == View.LAYOUT_DIRECTION_RTL ? ViewCompat.LAYOUT_DIRECTION_RTL : ViewCompat.LAYOUT_DIRECTION_LTR;
        if (viewCompatLayoutDirection != this.layoutDirection) {
            PagerAdapter adapter = super.getAdapter();
            int position = 0;
            if (adapter != null) {
                position = getCurrentItem();
            }
            this.layoutDirection = viewCompatLayoutDirection;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                setCurrentItem(position);
            }
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (adapter != null) {
            adapter = new SidesAdapter(adapter);
        }
        super.setAdapter(adapter);
        setCurrentItem(0);
    }

    @Override
    public PagerAdapter getAdapter() {
        SidesAdapter adapter = (SidesAdapter) super.getAdapter();
        return adapter == null ? null : adapter.getDelegate();
    }

    private boolean isRtl() {
        return layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    @Override
    public int getCurrentItem() {
        int item = super.getCurrentItem();
        PagerAdapter adapter = super.getAdapter();
        if (adapter != null && isRtl()) {
            item = adapter.getCount() - item - 1;
        }
        return item;
    }

    @Override
    public void setCurrentItem(int position, boolean smoothScroll) {
        PagerAdapter adapter = super.getAdapter();
        if (adapter != null && isRtl()) {
            position = adapter.getCount() - position - 1;
        }
        super.setCurrentItem(position, smoothScroll);
    }

    @Override
    public void setCurrentItem(int position) {
        PagerAdapter adapter = super.getAdapter();
        if (adapter != null && isRtl()) {
            position = adapter.getCount() - position - 1;
        }
        super.setCurrentItem(position);
    }

    @Deprecated
    @Override
    public void setOnPageChangeListener(@NonNull ViewPager.OnPageChangeListener listener) {
        super.setOnPageChangeListener(new SidesPageChangeListener(listener));
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, layoutDirection);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        layoutDirection = ss.mLayoutDirection;
        super.onRestoreInstanceState(ss.mViewPagerSavedState);
    }

    public static class SavedState implements Parcelable {

        private final Parcelable mViewPagerSavedState;
        private final int mLayoutDirection;

        private SavedState(Parcelable viewPagerSavedState, int layoutDirection) {
            mViewPagerSavedState = viewPagerSavedState;
            mLayoutDirection = layoutDirection;
        }

        private SavedState(Parcel in, ClassLoader loader) {
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            mViewPagerSavedState = in.readParcelable(loader);
            mLayoutDirection = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeParcelable(mViewPagerSavedState, flags);
            out.writeInt(mLayoutDirection);
        }

        public static final Parcelable.ClassLoaderCreator<SavedState> CREATOR
                = new Parcelable.ClassLoaderCreator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return createFromParcel(source, null);
            }

            @Override
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return new SavedState(source, loader);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

    }

    @Override
    public void addOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        SidesPageChangeListener reversingListener = new SidesPageChangeListener(listener);
        mPageChangeListeners.put(listener, reversingListener);
        super.addOnPageChangeListener(reversingListener);
    }

    @Override
    public void removeOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        SidesPageChangeListener reverseListener = mPageChangeListeners.remove(listener);
        if (reverseListener != null) {
            super.removeOnPageChangeListener(reverseListener);
        }
    }

    @Override
    public void clearOnPageChangeListeners() {
        super.clearOnPageChangeListeners();
        mPageChangeListeners.clear();
    }
    
    private class SidesPageChangeListener implements OnPageChangeListener {

        private final OnPageChangeListener mListener;

        SidesPageChangeListener(OnPageChangeListener listener) {
            mListener = listener;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            final int width = getWidth();
            PagerAdapter adapter = BothSideViewPager.super.getAdapter();
            if (isRtl() && adapter != null) {
                int count = adapter.getCount();
                int remainingWidth = (int) (width * (1 - adapter.getPageWidth(position))) + positionOffsetPixels;
                while (position < count && remainingWidth > 0) {
                    position += 1;
                    remainingWidth -= (int) (width * adapter.getPageWidth(position));
                }
                position = count - position - 1;
                positionOffsetPixels = -remainingWidth;
                positionOffset = positionOffsetPixels / (width * adapter.getPageWidth(position));
            }
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            PagerAdapter adapter = BothSideViewPager.super.getAdapter();
            if (isRtl() && adapter != null) {
                position = adapter.getCount() - position - 1;
            }
            mListener.onPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    private class SidesAdapter extends PagerAdapterDelegate {

        SidesAdapter(@NonNull PagerAdapter adapter) {
            super(adapter);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (isRtl()) {
                position = getCount() - position - 1;
            }
            super.destroyItem(container, position, object);
        }

        @Deprecated
        @Override
        public void destroyItem(@NonNull View container, int position, @NonNull Object object) {
            if (isRtl()) {
                position = getCount() - position - 1;
            }
            super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            int position = super.getItemPosition(object);
            if (isRtl()) {
                if (position == POSITION_UNCHANGED || position == POSITION_NONE) {
                    position = POSITION_NONE;
                } else {
                    position = getCount() - position - 1;
                }
            }
            return position;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (isRtl()) {
                position = getCount() - position - 1;
            }
            return super.getPageTitle(position);
        }

        @Override
        public float getPageWidth(int position) {
            if (isRtl()) {
                position = getCount() - position - 1;
            }
            return super.getPageWidth(position);
        }

        @Override
        public @NonNull
        Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (isRtl()) {
                position = getCount() - position - 1;
            }
            return super.instantiateItem(container, position);
        }

        @Deprecated
        @Override
        public @NonNull
        Object instantiateItem(@NonNull View container, int position) {
            if (isRtl()) {
                position = getCount() - position - 1;
            }
            return super.instantiateItem(container, position);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (isRtl()) {
                position = getCount() - position - 1;
            }
            super.setPrimaryItem(container, position, object);
        }

        @Deprecated
        @Override
        public void setPrimaryItem(@NonNull View container, int position, @NonNull Object object) {
            if (isRtl()) {
                position = getCount() - position - 1;
            }
            super.setPrimaryItem(container, position, object);
        }

    }
}