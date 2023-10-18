package com.inappstory.sdk.core.repository.session;

public interface ISessionRepository extends IStatisticPermission {
    void getSession(IGetSessionCallback getSessionCallback);
}
