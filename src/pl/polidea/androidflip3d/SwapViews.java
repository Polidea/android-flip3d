package pl.polidea.androidflip3d;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Swaps two image views. Starts 3D animation in order to do that.
 * 
 */
public final class SwapViews implements Runnable {
    private final boolean mIsFirstView;
    private final View view1;
    private final View view2;

    public SwapViews(final boolean isFirstView, final View view1,
            final View view2) {
        mIsFirstView = isFirstView;
        this.view1 = view1;
        this.view2 = view2;
    }

    @Override
    public void run() {
        final float centerX = view1.getWidth() / 2.0f;
        final float centerY = view1.getHeight() / 2.0f;
        Flip3DAnimation rotation;

        if (mIsFirstView) {
            view1.setVisibility(View.INVISIBLE);
            view2.setVisibility(View.VISIBLE);
            view2.requestFocus();
            view2.setClickable(true);

            rotation = new Flip3DAnimation(90, 0, centerX, centerY);
        } else {
            view2.setVisibility(View.INVISIBLE);
            view1.setVisibility(View.VISIBLE);
            view1.requestFocus();
            view1.setClickable(true);
            rotation = new Flip3DAnimation(-90, 0, centerX, centerY);
        }

        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new DecelerateInterpolator());

        if (mIsFirstView) {
            view2.startAnimation(rotation);
        } else {
            view1.startAnimation(rotation);
        }
    }
}