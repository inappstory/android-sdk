package com.inappstory.sdk.core.ui.widgets.bs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface BottomSheet {

    /**
     * <br>
     * Shows the actual sheet.
     * <br>
     * (Whether the sheet animation will be played or not depends on the underlying implementation,
     * in most cases it should be played by default)
     * <br>
     */
    void show();

    /**
     * Shows the actual sheet.
     *
     * @param animate whether to animate the sheet or not.
     */
    void show(boolean animate);

    /**
     * <br>
     * Dismisses the actual sheet.
     * <br>
     * (Whether the sheet animation will be played or not depends on the underlying implementation,
     * in most cases it should be played by default)
     * <br>
     */
    void dismiss();

    /**
     * Dismisses the actual sheet.
     *
     * @param animate whether to animate the sheet or not.
     */
    void dismiss(boolean animate);

    /**
     * Registers a listener which will be triggered when a dismissal event happens.
     *
     * @param onDismissListener the listener
     */
    void setOnDismissListener(@Nullable OnDismissListener onDismissListener);

    /**
     * Retrieves the current state of the sheet.
     *
     * @return the sheet's {@link State}
     */
    @NonNull
    State getState();

    /**
     * The possible states of the Bottom Sheet.
     */
    enum State {

        COLLAPSED,
        COLLAPSING,
        EXPANDED,
        EXPANDING

    }

    /**
     * Bottom Sheet Dismissal Listener.
     */
    interface OnDismissListener {

        /**
         * Called when the bottom sheet dismissal happens.
         *
         * @param bottomSheet the dismissed bottom sheet
         */
        void onDismiss(@NonNull BottomSheet bottomSheet);

    }

}