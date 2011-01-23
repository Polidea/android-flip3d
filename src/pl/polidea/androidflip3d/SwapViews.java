package pl.polidea.androidflip3d;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Swaps two image views. Starts 3D animation in order to do that.
 * 
 */
public final class SwapViews implements Runnable {
    private final boolean mIsFirstView;
    private final View image1;
    private final View image2;

    public SwapViews(final boolean isFirstView, final View image1, final View image2) {
        mIsFirstView = isFirstView;
        this.image1 = image1;
        this.image2 = image2;
    }

    @Override
    public void run() {
        final float centerX = image1.getWidth() / 2.0f;
        final float centerY = image1.getHeight() / 2.0f;
        Flip3DAnimation rotation;

        if (mIsFirstView) {
            image1.setVisibility(View.INVISIBLE);
            image2.setVisibility(View.VISIBLE);
            image2.requestFocus();

            rotation = new Flip3DAnimation(90, 0, centerX, centerY);
        } else {
            image2.setVisibility(View.INVISIBLE);
            image1.setVisibility(View.VISIBLE);
            image1.requestFocus();
            rotation = new Flip3DAnimation(-90, 0, centerX, centerY);
        }

        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new DecelerateInterpolator());

        if (mIsFirstView) {
            image2.startAnimation(rotation);
        } else {
            image1.startAnimation(rotation);
        }
    }
}