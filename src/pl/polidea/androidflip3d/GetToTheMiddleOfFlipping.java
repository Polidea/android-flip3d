package pl.polidea.androidflip3d;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * Chooses correct view during animation. Swaps them when animation ends.
 * 
 */
public final class GetToTheMiddleOfFlipping implements
        Animation.AnimationListener {
    private static final String TAG = GetToTheMiddleOfFlipping.class
            .getSimpleName();

    private final int originalViewIndex;
    private final View[] views;
    private final long animationLength;
    private final int direction;
    private final AnimationListener finishFlippingListener;

    /**
     * Animation listener for continuing flipping when reached the middle.
     * 
     * @param originalViewIndex
     *            current view index
     * @param views
     *            views
     * @param animationLength
     *            length of animation
     * @param direction
     *            direction in which to animate
     * @param finishFlippingListener
     *            listener to run on finishing flipping
     */
    public GetToTheMiddleOfFlipping(final int originalViewIndex,
            final View[] views, final long animationLength, // NOPMD
            final int direction, final AnimationListener finishFlippingListener) {
        this.originalViewIndex = originalViewIndex;
        this.views = views;
        this.animationLength = animationLength;
        this.direction = direction;
        this.finishFlippingListener = finishFlippingListener;
    }

    @Override
    public void onAnimationStart(final Animation animation) {
        Log.v(TAG, "Animation started.");
    }

    @Override
    public void onAnimationEnd(final Animation animation) {
        views[originalViewIndex].post(new SwapViews(originalViewIndex, views,
                animationLength, direction, finishFlippingListener));
        Log.v(TAG, "Animation finished.");
    }

    @Override
    public void onAnimationRepeat(final Animation animation) {
    }

}
