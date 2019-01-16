package com.zofers.zofers.service;

public interface ErrorCodeCallback<T> {
        void onResponse(T response);

        void onFailure();

        void onError(int code);
    }