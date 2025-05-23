package com.ablanco.zoomy;

import android.app.Activity;
import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.view.Window;
import android.view.animation.Interpolator;

public class Zoomy {

    private static ZoomyConfig mDefaultConfig = new ZoomyConfig();

    private Zoomy() {
    }

    public static void setDefaultConfig(ZoomyConfig config) {
        mDefaultConfig = config;
    }

    public static void unregister(@NonNull View view) {
        view.setOnTouchListener(null);
    }

    public static class Builder {

        private boolean mDisposed = false;

        private ZoomyConfig mConfig;
        private final TargetContainer mTargetContainer;
        private View mTargetView;
        private ZoomListener mZoomListener;
        private Interpolator mZoomInterpolator;
        private TapListener mTapListener;
        private LongPressListener mLongPressListener;
        private DoubleTapListener mdDoubleTapListener;
        private final Window mWindow;

        public Builder(Activity activity) {
            this.mTargetContainer = new ActivityContainer(activity);
            this.mWindow = activity.getWindow();
        }

        public Builder(Dialog dialog) {
            this.mTargetContainer = new DialogContainer(dialog);
            this.mWindow = dialog.getWindow();
        }

        public Builder(DialogFragment dialogFragment) {
            this.mTargetContainer = new DialogFragmentContainer(dialogFragment);
            Dialog dialog = dialogFragment.requireDialog();
            this.mWindow = dialog.getWindow();
        }

        public Builder target(View target) {
            this.mTargetView = target;
            return this;
        }

        public Builder animateZooming(boolean animate) {
            checkNotDisposed();
            if (mConfig == null) mConfig = new ZoomyConfig();
            this.mConfig.setZoomAnimationEnabled(animate);
            return this;
        }

        public Builder enableImmersiveMode(boolean enable) {
            checkNotDisposed();
            if (mConfig == null) mConfig = new ZoomyConfig();
            this.mConfig.setImmersiveModeEnabled(enable);
            return this;
        }

        public Builder enableShadow(boolean enable) {
            checkNotDisposed();
            if (mConfig == null) mConfig = new ZoomyConfig();
            this.mConfig.setShadowEnabled(enable);
            return this;
        }

        public Builder interpolator(Interpolator interpolator) {
            checkNotDisposed();
            this.mZoomInterpolator = interpolator;
            return this;
        }

        public Builder zoomListener(ZoomListener listener) {
            checkNotDisposed();
            this.mZoomListener = listener;
            return this;
        }

        public Builder tapListener(TapListener listener) {
            checkNotDisposed();
            this.mTapListener = listener;
            return this;
        }

        public Builder longPressListener(LongPressListener listener) {
            checkNotDisposed();
            this.mLongPressListener = listener;
            return this;
        }


        public Builder doubleTapListener(DoubleTapListener listener) {
            checkNotDisposed();
            this.mdDoubleTapListener = listener;
            return this;
        }

        public void register() {
            checkNotDisposed();
            if (mConfig == null) mConfig = mDefaultConfig;
            if (mTargetView == null)
                throw new IllegalArgumentException("Target view must not be null");
            mTargetView.setOnTouchListener(new ZoomableTouchListener(mTargetContainer, mTargetView,
                    mConfig, mZoomInterpolator, mZoomListener, mTapListener, mLongPressListener,
                    mdDoubleTapListener, mWindow));
            mDisposed = true;
        }

        private void checkNotDisposed() {
            if (mDisposed) throw new IllegalStateException("Builder already disposed");
        }
    }
}
