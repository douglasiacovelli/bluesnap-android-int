package com.bluesnap.androidapi.services;

import android.support.annotation.MainThread;

/**
 * An interface to run async callbacks from services on the main thread.
 */
public interface BluesnapServiceCallback {
    @MainThread
    void onSuccess();

    @MainThread
    void onFailure();
}
