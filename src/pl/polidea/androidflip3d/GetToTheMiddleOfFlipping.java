package pl.polidea.androidflip3d;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * Chooses correct view during animation. Swaps them when animation ends.
 * 
 */
public final class GetToTheMiddleOfFlipping implements
        Animation.AnimationListener {
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
     */
    public GetToTheMiddleOfFlipping(final int originalViewIndex,
            final View[] views, final long animationLength,
            final int direction, final AnimationListener finishFlippingListener) {
        this.originalViewIndex = originalViewIndex;
        this.views = views;
        this.animationLength = animationLength;
        this.direction = direction;
        this.finishFlippingListener = finishFlippingListener;
    }

    @Override
    public void onAnimationStart(final Animation animation) {
        // do nothing. No need
    }

    @Override
    public void onAnimationEnd(final Animation animation) {
        views[originalViewIndex].post(new SwapViews(originalViewIndex, views,
                animationLength, direction, finishFlippingListener));
    }

    @Override
    public void onAnimationRepeat(final Animation animation) {
        // do nothing. No need
    }

}
