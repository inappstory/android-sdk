package com.inappstory.sdk.core.repository.session;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.dto.StatisticPermissionDTO;
import com.inappstory.sdk.core.repository.session.dto.UgcEditorDTO;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionCallback;
import com.inappstory.sdk.core.repository.session.interfaces.IPlaceholdersDtoHolder;
import com.inappstory.sdk.core.repository.session.interfaces.IUpdateSessionCallback;
import com.inappstory.sdk.core.repository.session.usecase.CloseSession;
import com.inappstory.sdk.core.repository.session.usecase.OpenSession;
import com.inappstory.sdk.core.repository.session.usecase.UpdateSession;
import com.inappstory.sdk.core.models.api.CacheFontObject;
import com.inappstory.sdk.core.models.api.SessionResponse;
import com.inappstory.sdk.core.models.StoryPlaceholder;
import com.inappstory.sdk.core.repository.statistic.IStatisticV1Repository;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.filedownloader.FileDownloadCallbackAdapter;

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

    private IPlaceholdersDtoHolder placeholdersDtoHolder;

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

        new OpenSession().open(
                context,
                userId,
                new IGetSessionCallback<SessionResponse>() {
                    @Override
                    public void onSuccess(SessionResponse session) {
                        IASCore.getInstance().statisticV1Repository.refreshStatisticProcess();
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
                            placeholdersDtoHolder = new PlaceholdersDtoHolder(
                                    session.placeholders,
                                    session.imagePlaceholders
                            );
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
                        if (CallbackManager.getInstance().getErrorCallback() != null) {
                            CallbackManager.getInstance().getErrorCallback().sessionError();
                        }
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
                IASCore.getInstance().filesRepository.getFont(
                        cacheFontObject.url,
                        new FileDownloadCallbackAdapter()
                );
            }
        }
    }


    @Override
    public void closeSession() {
        IStatisticV1Repository statisticV1Repository = IASCore.getInstance().statisticV1Repository;
        statisticV1Repository.completeCurrentStatisticRecord();
        List<List<Object>> stat = new ArrayList<>(
                isAllowStatV1() ?
                        statisticV1Repository.getCurrentStatistic() :
                        new ArrayList<List<Object>>()
        );
        statisticV1Repository.clear();
        new CloseSession().close(
                sessionDTO,
                stat
        );
        sessionDTO = null;
        ugcEditorDTO = null;
        placeholdersDtoHolder = null;
        statisticPermissionDTO = null;
    }

    @Override
    public void updateSession(final List<List<Object>> sendingStatistic, final IUpdateSessionCallback callback) {
        new UpdateSession().update(sessionDTO, sendingStatistic, callback);
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
            return InAppStoryManager.getInstance().isSendStatistic() && statisticPermissionDTO.isAllowStatV1();
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
        if (placeholdersDtoHolder != null)
            return placeholdersDtoHolder.getImagePlaceholders();
        return new ArrayList<>();
    }

    @Override
    public List<StoryPlaceholder> getTextPlaceholders() {
        if (placeholdersDtoHolder != null)
            return placeholdersDtoHolder.getTextPlaceholders();
        return new ArrayList<>();
    }

    @Override
    public UgcEditorDTO getUgcEditor() {
        return ugcEditorDTO;
    }
}
