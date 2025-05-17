package com.ablanco.zoomy;

import android.graphics.PointF;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

class MotionUtils {

    static void midPointOfEvent(PointF point, @NonNull MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        }
    }
}
