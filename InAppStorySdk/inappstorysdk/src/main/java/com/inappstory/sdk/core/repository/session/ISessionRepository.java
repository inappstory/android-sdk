package com.inappstory.sdk.core.repository.session;

import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionCallback;
import com.inappstory.sdk.core.repository.session.interfaces.IPlaceholdersDtoHolder;
import com.inappstory.sdk.core.repository.session.interfaces.IStatisticPermission;
import com.inappstory.sdk.core.repository.session.interfaces.IUgcEditorDtoHolder;

public interface ISessionRepository extends
        IStatisticPermission,
        IUgcEditorDtoHolder,
        IPlaceholdersDtoHolder {
    void getSession(
            String userId,
            IGetSessionCallback<SessionDTO> getSessionCallback
    );

    SessionDTO getSessionData();

    void closeSession();

    void changeSession(
            String newUserId,
            IGetSessionCallback<SessionDTO> getSessionCallback
    );
}
