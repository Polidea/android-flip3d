package pl.polidea.androidflip3d;

import android.view.View;
import android.view.animation.Animation;

/**
 * Chooses correct view during animation. Swaps them when animation ends.
 * 
 */
public final class DisplayNextView implements Animation.AnimationListener {
    private final boolean mCurrentView;
    private final View view1;
    private final View view2;

    public DisplayNextView(final boolean currentView, final View view1, final View view2) {
        mCurrentView = currentView;
        this.view1 = view1;
        this.view2 = view2;
    }

    @Override
    public void onAnimationStart(final Animation animation) {
        // do nothing. No need
    }

    @Override
    public void onAnimationEnd(final Animation animation) {
        view1.post(new SwapViews(mCurrentView, view1, view2));
    }

    @Override
    public void onAnimationRepeat(final Animation animation) {
        // do nothing. No need
    }

}
