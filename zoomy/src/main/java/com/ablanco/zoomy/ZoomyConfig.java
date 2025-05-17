package com.ablanco.zoomy;

public class ZoomyConfig {

    private boolean zoomAnimationEnabled = true;
    private boolean immersiveModeEnabled = true;
    private boolean shadowEnabled = true;

    public boolean isZoomAnimationEnabled() {
        return zoomAnimationEnabled;
    }

    public void setZoomAnimationEnabled(boolean zoomAnimationEnabled) {
        this.zoomAnimationEnabled = zoomAnimationEnabled;
    }

    public boolean isImmersiveModeEnabled() {
        return immersiveModeEnabled;
    }

    public void setImmersiveModeEnabled(boolean immersiveModeEnabled) {
        this.immersiveModeEnabled = immersiveModeEnabled;
    }

    public boolean isShadowEnabled() {
        return shadowEnabled;
    }

    public void setShadowEnabled(boolean shadowEnabled) {
        this.shadowEnabled = shadowEnabled;
    }
}
