package com.zofers.zofers.service;

public interface ServiceCallback<T> {
        void onSuccess(T response);

        void onFailure();
    }