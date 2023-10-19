package com.inappstory.sdk.core.repository.session;

import com.inappstory.sdk.core.repository.session.dto.SessionDTO;

public interface ISessionRepository extends
        IStatisticPermission,
        IUgcEditorDtoHolder,
        IPlaceholdersDtoHolder {
    void getSession(
            String userId,
            IGetSessionCallback<SessionDTO> getSessionCallback
    );

    void closeSession();

    void changeSession(
            String newUserId,
            IGetSessionCallback<SessionDTO> getSessionCallback
    );
}
