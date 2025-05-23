package com.ablanco.zoomy;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

class ZoomableTouchListener implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private final Window window;

    private static final int STATE_IDLE = 0;
    private static final int STATE_POINTER_DOWN = 1;
    private static final int STATE_ZOOMING = 2;

    private static final float MIN_SCALE_FACTOR = 1f;
    private static final float MAX_SCALE_FACTOR = 5f;
    private final TapListener mTapListener;
    private final LongPressListener mLongPressListener;
    private final DoubleTapListener mDoubleTapListener;
    private int mState = STATE_IDLE;
    private final TargetContainer mTargetContainer;
    private final View mTarget;
    private ImageView mZoomableView;
    private View mShadow;
    private final ScaleGestureDetector mScaleGestureDetector;
    private final GestureDetector mGestureDetector;
    private float mScaleFactor = 1f;
    private PointF mCurrentMovementMidPoint = new PointF();
    private PointF mInitialPinchMidPoint = new PointF();
    private Point mTargetViewCords = new Point();
    private boolean mAnimatingZoomEnding = false;
    private final Interpolator mEndZoomingInterpolator;
    private final ZoomyConfig mConfig;
    private final ZoomListener mZoomListener;
    private final Runnable mEndingZoomAction = new Runnable() {
        @Override
        public void run() {
            if (mConfig.isShadowEnabled()) {
                removeFromDecorView(mShadow);
            }
            removeFromDecorView(mZoomableView);
            mTarget.setVisibility(View.VISIBLE);
            mZoomableView = null;
            mCurrentMovementMidPoint = new PointF();
            mInitialPinchMidPoint = new PointF();
            mAnimatingZoomEnding = false;
            mState = STATE_IDLE;

            if (mZoomListener != null) mZoomListener.onViewEndedZooming(mTarget);

            if (mConfig.isImmersiveModeEnabled()) showSystemUI();
        }
    };

    ZoomableTouchListener(TargetContainer targetContainer,
                          @NonNull View view,
                          ZoomyConfig config,
                          Interpolator interpolator,
                          ZoomListener zoomListener,
                          TapListener tapListener,
                          LongPressListener longPressListener,
                          DoubleTapListener doubleTapListener,
                          Window window) {
        this.mTargetContainer = targetContainer;
        this.mTarget = view;
        this.mConfig = config;
        this.window = window;
        this.mEndZoomingInterpolator = interpolator != null
                ? interpolator : new AccelerateDecelerateInterpolator();
        this.mScaleGestureDetector = new ScaleGestureDetector(view.getContext(), this);
        GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mTapListener != null) mTapListener.onTap(mTarget);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (mLongPressListener != null) mLongPressListener.onLongPress(mTarget);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mDoubleTapListener != null) mDoubleTapListener.onDoubleTap(mTarget);
                return true;
            }
        };
        this.mGestureDetector = new GestureDetector(view.getContext(), mGestureListener);
        this.mZoomListener = zoomListener;
        this.mTapListener = tapListener;
        this.mLongPressListener = longPressListener;
        this.mDoubleTapListener = doubleTapListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        if (mAnimatingZoomEnding || ev.getPointerCount() > 2) {
            mEndingZoomAction.run();

            return true;
        }

        mScaleGestureDetector.onTouchEvent(ev);
        mGestureDetector.onTouchEvent(ev);

        int action = ev.getAction() & MotionEvent.ACTION_MASK;

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                switch (mState) {
                    case STATE_IDLE:
                        mState = STATE_POINTER_DOWN;
                        break;
                    case STATE_POINTER_DOWN:
                        mState = STATE_ZOOMING;
                        MotionUtils.midPointOfEvent(mInitialPinchMidPoint, ev);
                        startZoomingView(mTarget);
                        break;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mState == STATE_ZOOMING) {
                    MotionUtils.midPointOfEvent(mCurrentMovementMidPoint, ev);
                    //because our initial pinch could be performed in any of the view edges,
                    //we need to subtract this difference and add system bars height
                    //as an offset to avoid an initial transition jump
                    mCurrentMovementMidPoint.x -= mInitialPinchMidPoint.x;
                    mCurrentMovementMidPoint.y -= mInitialPinchMidPoint.y;
                    //because previous function returns the midpoint for relative X, Y coordinates,
                    //we need to add absolute view coordinates in order to ensure the correct position
                    mCurrentMovementMidPoint.x += mTargetViewCords.x;
                    mCurrentMovementMidPoint.y += mTargetViewCords.y;
                    float x = mCurrentMovementMidPoint.x;
                    float y = mCurrentMovementMidPoint.y;
                    mZoomableView.setX(x);
                    mZoomableView.setY(y);
                }
                break;

            case MotionEvent.ACTION_UP:
                v.performClick();

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                switch (mState) {
                    case STATE_ZOOMING:
                        endZoomingView();
                        break;
                    case STATE_POINTER_DOWN:
                        mState = STATE_IDLE;
                        break;
                }
                break;
        }
        return true;
    }

    private void endZoomingView() {
        if (mConfig.isZoomAnimationEnabled()) {
            mAnimatingZoomEnding = true;
            mZoomableView.animate()
                    .x(mTargetViewCords.x)
                    .y(mTargetViewCords.y)
                    .scaleX(1)
                    .scaleY(1)
                    .setInterpolator(mEndZoomingInterpolator)
                    .withEndAction(mEndingZoomAction).start();
        } else mEndingZoomAction.run();
    }

    private void startZoomingView(View view) {
        mZoomableView = new ImageView(mTarget.getContext());
        mZoomableView.setLayoutParams(new ViewGroup.LayoutParams(mTarget.getWidth(), mTarget.getHeight()));
        mZoomableView.setImageBitmap(ViewUtils.getBitmapFromView(view));

        mTargetViewCords = ViewUtils.getViewAbsoluteCords(view);

        mZoomableView.setX(mTargetViewCords.x);
        mZoomableView.setY(mTargetViewCords.y);

        if (mConfig.isShadowEnabled()) {
            if (mShadow == null) mShadow = new View(mTarget.getContext());
            mShadow.setBackgroundResource(0);
            addToDecorView(mShadow);
        }

        addToDecorView(mZoomableView);

        disableParentTouch(mTarget.getParent());
        mTarget.setVisibility(View.INVISIBLE);

        if (mConfig.isImmersiveModeEnabled()) hideSystemUI();
        if (mZoomListener != null) mZoomListener.onViewStartedZooming(mTarget);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (mZoomableView == null) return false;

        mScaleFactor *= detector.getScaleFactor();

        mScaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min(mScaleFactor, MAX_SCALE_FACTOR));

        mZoomableView.setScaleX(mScaleFactor);
        mZoomableView.setScaleY(mScaleFactor);
        if (mConfig.isShadowEnabled()) {
            obscureDecorView(mScaleFactor);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return mZoomableView != null;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        mScaleFactor = 1f;
    }

    private void addToDecorView(View v) {
        mTargetContainer.getDecorView().addView(v);
    }

    private void removeFromDecorView(View v) {
        mTargetContainer.getDecorView().removeView(v);
    }

    private void obscureDecorView(float factor) {
        float normalizedValue = (factor - MIN_SCALE_FACTOR) / (MAX_SCALE_FACTOR - MIN_SCALE_FACTOR);
        normalizedValue = Math.min(0.75f, normalizedValue * 2);
        int obscure = Color.argb((int) (normalizedValue * 255), 0, 0, 0);
        mShadow.setBackgroundColor(obscure);
    }

    private void hideSystemUI() {
        if (window == null) return;
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    private void showSystemUI() {
        if (window == null) return;
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        controller.show(WindowInsetsCompat.Type.navigationBars());

        boolean isFullscreen = isInFullscreenMode(window);
        if (isFullscreen) {
            controller.hide(WindowInsetsCompat.Type.statusBars());
        } else {
            controller.show(WindowInsetsCompat.Type.statusBars());
        }

        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    private boolean isInFullscreenMode(@NonNull Window window) {
        return isFullScreenTheme(window);
    }

    private boolean isFullScreenTheme(@NonNull Window window) {
        try (TypedArray a = window.getContext().obtainStyledAttributes(
                new int[]{android.R.attr.windowFullscreen})) {
            return a.getBoolean(0, false);
        }
    }

    private void disableParentTouch(@NonNull ViewParent view) {
        view.requestDisallowInterceptTouchEvent(true);
        if (view.getParent() != null) disableParentTouch((view.getParent()));
    }
}
