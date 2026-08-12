package com.prexlauncher.core.customcontrols.mouse;

import android.view.MotionEvent;
import android.view.View;

public interface TouchEventProcessor {
    boolean processTouchEvent(MotionEvent motionEvent);
    void cancelPendingActions();
    default void dispatchTouchEvent(MotionEvent event, View view) {}
}
