package com.inappstory.sdk.game.reader;

import android.content.Context;

import java.util.Map;
import java.util.concurrent.Callable;

public class GameRequestAsync implements Callable<GameResponse> {

        String method;
        String path;
        Map<String, String> headers;
        Map<String, String> getParams;
        String body;
        String requestId;
        Context context;

        public GameRequestAsync(String method, String path,
                                Map<String, String> headers,
                                Map<String, String> getParams,
                                String body, String requestId,
                                Context context) {
            this.method = method;
            this.path = path;
            this.headers = headers;
            this.getParams = getParams;
            this.body = body;
            this.requestId = requestId;
            this.context = context;
        }

        @Override
        public GameResponse call() throws Exception {
            try {
                GameResponse s = GameNetwork.sendRequest(method, path, headers,
                        getParams, body, requestId, context);
                return s;
            } catch (Exception e) {
                GameResponse response = new GameResponse();
                response.status = 12002;
                return response;
            }
        }
    }