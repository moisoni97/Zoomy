package com.ablanco.zoomy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;

import androidx.annotation.NonNull;

class ViewUtils {

    static Bitmap getBitmapFromView(@NonNull View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(returnedBitmap);
        view.draw(canvas);
        return returnedBitmap;
    }

    @NonNull
    static Point getViewAbsoluteCords(@NonNull View v) {
        int[] location = new int[2];
        v.getLocationInWindow(location);
        int x = location[0];
        int y = location[1];

        return new Point(x, y);
    }

    static void viewMidPoint(@NonNull PointF point, @NonNull View v) {
        float x = v.getWidth();
        float y = v.getHeight();
        point.set(x / 2, y / 2);
    }
}
