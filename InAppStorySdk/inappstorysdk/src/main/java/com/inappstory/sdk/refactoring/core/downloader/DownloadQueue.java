package com.inappstory.sdk.refactoring.core.downloader;

import java.util.ArrayList;
import java.util.List;

public class DownloadQueue<T> {
    private final List<T> ids = new ArrayList<>();

    public boolean contains(T id) {
        return ids.contains(id);
    }

    public T pop() {
        if (ids.isEmpty()) return null;
        return ids.remove(0);
    }

    public List<T> retrieve() {
        List<T> copy = new ArrayList<>(ids);
        ids.clear();
        return copy;
    }

    public void remove(T id) {
        ids.remove(id);
    }

    public void push(T id) {
        ids.remove(id);
        ids.add(0, id);
    }

    public void clear() {
        ids.clear();
    }
}
