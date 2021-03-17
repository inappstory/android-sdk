package com.inappstory.sdk.imageloader;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MemoryCache {
    private Map<String, SoftReference<Bitmap>> cache = Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());
    private Map<String, String> cacheSettings = Collections.synchronizedMap(new HashMap<String, String>());

    //Test
    public Bitmap get(String id){
        if(!cache.containsKey(id))
            return null;
        SoftReference<Bitmap> ref=cache.get(id);
        return ref.get();
    }


    public String getSettings(String id){
        if(!cacheSettings.containsKey(id))
            return null;
        return cacheSettings.get(id);
    }

    //Test
    public void put(String id, Bitmap bitmap){
        cache.put(id, new SoftReference<Bitmap>(bitmap));
    }

    public void putSettings(String id, String settings){
        cacheSettings.put(id, settings);
    }

    public void clear() {
        cache.clear();
        cacheSettings.clear();
    }
}