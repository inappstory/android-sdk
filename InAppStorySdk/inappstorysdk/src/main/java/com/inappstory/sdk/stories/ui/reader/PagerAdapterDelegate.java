package com.inappstory.sdk.stories.ui.reader;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class PagerAdapterDelegate extends PagerAdapter {

    private final PagerAdapter mDelegate;

    PagerAdapterDelegate(@NonNull final PagerAdapter delegate) {
        this.mDelegate = delegate;
        delegate.registerDataSetObserver(new MyDataSetObserver(this));
    }

    PagerAdapter getDelegate() {
        return mDelegate;
    }

    @Override
    public int getCount() {
        return mDelegate.getCount();
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        mDelegate.startUpdate(container);
    }

    @Override
    public @NonNull
    Object instantiateItem(@NonNull ViewGroup container, int position) {
        return mDelegate.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mDelegate.destroyItem(container, position, object);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mDelegate.setPrimaryItem(container, position, object);
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        mDelegate.finishUpdate(container);
    }

    @Deprecated
    @Override
    public void startUpdate(@NonNull View container) {
        mDelegate.startUpdate(container);
    }

    @Deprecated
    @Override
    public @NonNull
    Object instantiateItem(@NonNull View container, int position) {
        return mDelegate.instantiateItem(container, position);
    }

    @Deprecated
    @Override
    public void destroyItem(@NonNull View container, int position, @NonNull Object object) {
        mDelegate.destroyItem(container, position, object);
    }

    @Deprecated
    @Override
    public void setPrimaryItem(@NonNull View container, int position, @NonNull Object object) {
        mDelegate.setPrimaryItem(container, position, object);
    }

    @Deprecated
    @Override
    public void finishUpdate(@NonNull View container) {
        mDelegate.finishUpdate(container);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return mDelegate.isViewFromObject(view, object);
    }

    @Override
    public Parcelable saveState() {
        return mDelegate.saveState();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        mDelegate.restoreState(state, loader);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return mDelegate.getItemPosition(object);
    }

    @Override
    public void notifyDataSetChanged() {
        mDelegate.notifyDataSetChanged();
    }

    @Override
    public void registerDataSetObserver(@NonNull DataSetObserver observer) {
        mDelegate.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(@NonNull DataSetObserver observer) {
        mDelegate.unregisterDataSetObserver(observer);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mDelegate.getPageTitle(position);
    }

    @Override
    public float getPageWidth(int position) {
        return mDelegate.getPageWidth(position);
    }

    private void superNotifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private static class MyDataSetObserver extends DataSetObserver {

        final PagerAdapterDelegate mParent;

        private MyDataSetObserver(PagerAdapterDelegate mParent) {
            this.mParent = mParent;
        }

        @Override
        public void onChanged() {
            if (mParent != null) {
                mParent.superNotifyDataSetChanged();
            }
        }

        @Override
        public void onInvalidated() {
            onChanged();
        }

    }

}