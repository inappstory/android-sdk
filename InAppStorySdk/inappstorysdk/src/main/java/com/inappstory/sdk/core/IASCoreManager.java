package com.inappstory.sdk.core;

import android.content.Context;

import com.inappstory.sdk.core.repository.files.FilesRepository;
import com.inappstory.sdk.core.repository.files.IFilesRepository;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionCallback;
import com.inappstory.sdk.core.repository.session.ISessionRepository;
import com.inappstory.sdk.core.repository.session.SessionRepository;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.dto.UgcEditorDTO;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public void getSession(
            IGetSessionCallback<SessionDTO> callback
    ) {
        if (userId == null) {
            callback.onError();
            return;
        }
        sessionRepository.getSession(userId, callback);
    }

    public void closeSession() {
        sessionRepository.closeSession();
    }

    public void getUgcEditor(
            final IGetSessionCallback<UgcEditorDTO> callback
    ) {
        sessionRepository.getSession(userId, new IGetSessionCallback<SessionDTO>() {
            @Override
            public void onSuccess(SessionDTO session) {
                callback.onSuccess(sessionRepository.getUgcEditor());
            }

            @Override
            public void onError() {
                callback.onError();
            }
        });
    }

    public void init(Context context) {
        if (filesRepository == null) {
            filesRepository = new FilesRepository(context.getCacheDir());
            sessionRepository = new SessionRepository(context);
        }
    }
}
