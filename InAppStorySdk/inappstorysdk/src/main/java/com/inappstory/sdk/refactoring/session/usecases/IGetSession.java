package com.inappstory.sdk.refactoring.session.usecases;

import com.inappstory.sdk.refactoring.session.UniqueSessionParameters;

public interface IGetSession {
    void getSession(
            UniqueSessionParameters sessionParameters
    );
}
