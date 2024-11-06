package com.inappstory.sdk.core.ui.widgets.bs;

import androidx.annotation.NonNull;

public interface Builder<T> {

    /**
     * Constructs (builds) the final object.
     *
     * @return the constructed (built) object
     */
    @NonNull
    T build();

}