package com.zofers.zofers.service;

import com.zofers.zofers.staff.FileHelper;

public interface ServiceCallback<T> {

       FileHelper x = new FileHelper();

        void onSuccess(T response);

        void onFailure();

         default void onIs () {

         }
}