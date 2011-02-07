package pl.polidea.androidflip3d;

import android.view.View;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;

/**
 * Swaps two image views. After that continues the 3D animation.
 * 
 */
public final class SwapViews implements Runnable {
    private final int currentViewIndex;
    private final View[] views;
    private final long animationLength;
    private final int direction;
    private final AnimationListener finishFlippingListener;

    /**
     * Swaps the views and continues rotation.
     * 
     * @param originalViewIndex
     *            original index of the view
     * @param views
     *            views to swap
     * @param animationLenght
     *            length of the animation
     * @param direction
     *            direction of the animation
     * @param finishFlippingListener
     *            what to do when the whole animation is finished
     */
    public SwapViews(final int originalViewIndex, final View[] views, // NOPMD
            final long animationLenght, final int direction, final AnimationListener finishFlippingListener) {
        this.currentViewIndex = originalViewIndex;
        this.views = views;
        this.animationLength = animationLenght;
        this.direction = direction;
        this.finishFlippingListener = finishFlippingListener;
    }

    @Override
    public void run() {
        final int theOtherViewIndex = ViewIndex.getTheOtherViewIndex(currentViewIndex);
        final float centerX = views[theOtherViewIndex].getWidth() / 2.0f;
        final float centerY = views[theOtherViewIndex].getHeight() / 2.0f;
        views[currentViewIndex].setVisibility(View.INVISIBLE);
        views[theOtherViewIndex].setVisibility(View.VISIBLE);
        final Flip3DAnimation rotation = new Flip3DAnimation(-RotationDirection.getMultiplier(direction) * 90, 0,
                centerX, centerY);
        rotation.setDuration(animationLength);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new DecelerateInterpolator());
        rotation.setAnimationListener(finishFlippingListener);
        views[theOtherViewIndex].startAnimation(rotation);
    }
}