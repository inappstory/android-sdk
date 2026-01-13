package com.inappstory.sdk.core;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CancellationTokenPool {
    private final Map<String, CancellationTokenWithStatus> tokens = new HashMap<>();
    private final Object mapLock = new Object();

    private void removeOldTokens() {
        long curTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, CancellationTokenWithStatus>> it = tokens.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CancellationTokenWithStatus> entry = it.next();
            if (curTime - entry.getValue().creationTime() > 60000)
                it.remove();
        }

    }

    public CancellationTokenPool() {

    }

    public CancellationTokenWithStatus getTokenByUID(String tokenUID) {
        synchronized (mapLock) {
            return tokens.get(tokenUID);
        }
    }

    public void addToken(CancellationTokenWithStatus token) {
        synchronized (mapLock) {
            tokens.put(token.getUniqueId(), token);
            removeOldTokens();
        }
    }
}
