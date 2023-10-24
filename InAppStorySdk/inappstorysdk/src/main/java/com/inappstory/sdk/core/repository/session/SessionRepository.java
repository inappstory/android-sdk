package com.inappstory.sdk.core.repository.session;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCoreManager;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.dto.StatisticPermissionDTO;
import com.inappstory.sdk.core.repository.session.dto.UgcEditorDTO;
import com.inappstory.sdk.stories.api.models.CacheFontObject;
import com.inappstory.sdk.stories.api.models.SessionResponse;
import com.inappstory.sdk.stories.api.models.StoryPlaceholder;
import com.inappstory.sdk.stories.filedownloader.FileDownloadCallbackAdapter;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class SessionRepository implements ISessionRepository {
    final Context context;

    public SessionRepository(Context context) {
        this.context = context;
    }

    private SessionDTO sessionDTO;
    private StatisticPermissionDTO statisticPermissionDTO;
    private UgcEditorDTO ugcEditorDTO;
    private List<StoryPlaceholder> imagePlaceholders;
    private List<StoryPlaceholder> textPlaceholders;

    private SessionManager sessionManager = new SessionManager();

    private final Object openProcessLock = new Object();
    private List<IGetSessionCallback<SessionDTO>> getSessionCallbacks = new ArrayList<>();
    private boolean openProcess = false;

    @Override
    public void getSession(
            final String userId,
            IGetSessionCallback<SessionDTO> getSessionCallback
    ) {
        SessionDTO currentSession = checkSession();
        if (currentSession != null) {
            getSessionCallback.onSuccess(currentSession);
            return;
        }

        synchronized (openProcessLock) {
            getSessionCallbacks.add(getSessionCallback);
            if (openProcess) return;
            openProcess = true;
        }

        sessionManager.openSession(
                context,
                userId,
                new IGetSessionCallback<SessionResponse>() {
                    @Override
                    public void onSuccess(SessionResponse session) {
                        downloadFonts(session);
                        List<IGetSessionCallback<SessionDTO>> localCallbacks;
                        SessionDTO localDTO;
                        synchronized (openProcessLock) {
                            sessionDTO = new SessionDTO(
                                    session.session.id,
                                    userId,
                                    session.session.expireIn,
                                    session.getPreviewAspectRatio()
                            );
                            statisticPermissionDTO = new StatisticPermissionDTO(session);

                            if (session.editor != null)
                                ugcEditorDTO = new UgcEditorDTO(session.editor);

                            if (session.placeholders != null)
                                textPlaceholders = new ArrayList<>(session.placeholders);
                            else
                                textPlaceholders = new ArrayList<>();

                            if (session.imagePlaceholders != null)
                                imagePlaceholders = new ArrayList<>(session.imagePlaceholders);
                            else
                                imagePlaceholders = new ArrayList<>();

                            localDTO = sessionDTO;
                            openProcess = false;
                            localCallbacks = new ArrayList<>(getSessionCallbacks);
                            getSessionCallbacks.clear();
                        }
                        for (IGetSessionCallback<SessionDTO> callback : localCallbacks) {
                            callback.onSuccess(localDTO);
                        }
                    }

                    @Override
                    public void onError() {
                        List<IGetSessionCallback<SessionDTO>> localCallbacks;
                        synchronized (openProcessLock) {
                            openProcess = false;
                            localCallbacks = new ArrayList<>(getSessionCallbacks);
                            getSessionCallbacks.clear();
                        }
                        for (IGetSessionCallback<SessionDTO> callback : localCallbacks) {
                            callback.onError();
                        }
                    }
                }
        );
    }

    @Override
    public SessionDTO getSessionData() {
        return sessionDTO;
    }

    private void downloadFonts(@NonNull SessionResponse response) {
        if (response.cachedFonts != null) {
            for (CacheFontObject cacheFontObject : response.cachedFonts) {
                IASCoreManager.getInstance().filesRepository.getFont(
                        cacheFontObject.url,
                        new FileDownloadCallbackAdapter()
                );
            }
        }
    }


    @Override
    public void closeSession() {
        sessionManager.closeSession(
                sessionDTO,
                isAllowStatV1()
        );
        sessionDTO = null;
        ugcEditorDTO = null;
        textPlaceholders = null;
        imagePlaceholders = null;
        statisticPermissionDTO = null;
    }

    @Override
    public void changeSession(
            String newUserId,
            IGetSessionCallback<SessionDTO> getSessionCallback
    ) {
        if (sessionDTO.getId().equals(newUserId)) return;
        closeSession();
        getSession(newUserId, getSessionCallback);
    }


    private SessionDTO checkSession() {
        synchronized (openProcessLock) {
            return sessionDTO;
        }
    }

    @Override
    public boolean isAllowStatV1() {
        if (statisticPermissionDTO != null)
            return InAppStoryManager.getInstance().isSendStatistic() && statisticPermissionDTO.isAllowStatV1() ;
        return InAppStoryManager.getInstance().isSendStatistic();
    }

    @Override
    public boolean isAllowStatV2() {
        if (statisticPermissionDTO != null)
            return InAppStoryManager.getInstance().isSendStatistic() && statisticPermissionDTO.isAllowStatV2();
        return false;
    }

    @Override
    public boolean isAllowProfiling() {
        if (statisticPermissionDTO != null)
            return statisticPermissionDTO.isAllowProfiling();
        return false;
    }

    @Override
    public boolean isAllowCrash() {
        if (statisticPermissionDTO != null)
            return statisticPermissionDTO.isAllowCrash();
        return false;
    }

    @Override
    public List<StoryPlaceholder> getImagePlaceholders() {
        if (imagePlaceholders == null) imagePlaceholders = new ArrayList<>();
        return imagePlaceholders;
    }

    @Override
    public List<StoryPlaceholder> getTextPlaceholders() {
        if (textPlaceholders == null) textPlaceholders = new ArrayList<>();
        return textPlaceholders;
    }

    @Override
    public UgcEditorDTO getUgcEditor() {
        return ugcEditorDTO;
    }
}
