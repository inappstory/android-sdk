package com.inappstory.sdk.core.ui.widgets.bs;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public final class Preconditions {

    public static void isTrue(boolean condition) {
        isTrue("Condition", condition);
    }

    public static void isTrue(@NonNull String info, boolean condition) {
        nonNull(info);

        if (!condition) {
            throw new IllegalStateException(String.format(
                Locale.US,
                "%s - the condition is not met. The Condition must be positive.",
                info
            ));
        }
    }

    public static <T> T checkNonNull(T object) {
        nonNull(object);
        return object;
    }

    public static void nonNull(Object object) {
        if (object == null) {
            throw new NullPointerException("The argument must be non-null!");
        }
    }

    public static void nonEmpty(String string) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException("You must specify a valid raw text.");
        }
    }

    public static void nonEmpty(Collection<?> collection) {
        nonNull(collection);

        if (collection.isEmpty()) {
            throw new IllegalArgumentException("You must specify a collection that contains at least one element.");
        }
    }

    public static void withinBoundsExclusive(int index, ArrayList<?> dataset) {
        nonNull(dataset);

        if ((index < 0) || (index >= dataset.size())) {
            throw new IndexOutOfBoundsException("The Index must lie within the bounds of the specified dataset (0 <= index < dataset.size).");
        }
    }

    public static void withinBoundsInclusive(int index, ArrayList<?> dataset) {
        nonNull(dataset);

        if ((index < 0) || (index > dataset.size())) {
            throw new IndexOutOfBoundsException("The Index must lie within the bounds of the specified dataset (0 <= index <= dataset.size).");
        }
    }

}