package com.bluesnap.android.demoapp;

import android.support.test.espresso.IdlingResource;
import android.view.View;

import static com.bluesnap.android.demoapp.TestUtils.getCurrentActivity;

public class VisibleViewIdlingResource implements IdlingResource {

    private final int targetViewId;
    private final int targetVisibility;
    private ResourceCallback resourceCallback;
    private boolean isIdle;

    public VisibleViewIdlingResource(int targetView, int visible) {
        this.targetViewId = targetView;
        this.targetVisibility = visible;
    }

    @Override
    public String getName() {
        return VisibleViewIdlingResource.class.getName() + String.valueOf(targetViewId);
    }

    @Override
    public boolean isIdleNow() {
        if (isIdle) return true;

        if (getCurrentActivity() == null)
            return false;

        View targetView = getCurrentActivity().findViewById(targetViewId);
        if (targetView == null)
            return false;

        isIdle = targetVisibility == targetView.getVisibility();
        if (isIdle) {
            resourceCallback.onTransitionToIdle();
        }

        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}