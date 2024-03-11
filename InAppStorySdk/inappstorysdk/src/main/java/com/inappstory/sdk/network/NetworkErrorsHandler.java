package com.inappstory.sdk.network;


public interface NetworkErrorsHandler {

    void onError(int code, String message);

    void error400(String message);

    void error401(String message);

    void error402(String message);

    void error403(String message);

    void error404(String message);

    void error405(String message);

    void error409(String message);

    void error410(String message);

    void error415(String message);

    void error418(String message);

    void error422(String message);

    void error423(String message);

    void error424(String message);

    void error429(String message);

    void error500(String message);

    void error502(String message);

    void timeoutError();
    void connectionError();
    void jsonError(String message);
    void errorDefault(String message);
    void emptyContent();
}
