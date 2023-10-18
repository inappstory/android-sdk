package com.inappstory.sdk.core;

import android.content.Context;

import com.inappstory.sdk.core.repository.files.FilesRepository;
import com.inappstory.sdk.core.repository.files.IFilesRepository;
import com.inappstory.sdk.core.repository.session.IGetSessionCallback;
import com.inappstory.sdk.core.repository.session.ISessionRepository;
import com.inappstory.sdk.core.repository.session.SessionRepository;

public class IASCoreManager {
    private static IASCoreManager INSTANCE;
    private static final Object lock = new Object();

    public static IASCoreManager getInstance() {
        synchronized (lock) {
            if (INSTANCE == null)
                INSTANCE = new IASCoreManager();
            return INSTANCE;
        }
    }

    public IFilesRepository filesRepository;

    public ISessionRepository sessionRepository;

    public void getSession(IGetSessionCallback callback) {
        sessionRepository.getSession(callback);
    }

    public void init(Context context) {
        filesRepository = new FilesRepository(context.getCacheDir());
        sessionRepository = new SessionRepository();
    }
}
