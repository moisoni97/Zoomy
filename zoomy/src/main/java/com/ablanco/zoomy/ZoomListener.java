package com.ablanco.zoomy;

import android.view.View;

public interface ZoomListener {
    void onViewStartedZooming(View view);

    void onViewEndedZooming(View view);
}
