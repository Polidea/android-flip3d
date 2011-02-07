package pl.polidea.androidflip3d;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Animation used to flip image in 3D.
 * 
 */
public class Flip3DAnimation extends Animation {
    private final float mFromDegrees;
    private final float mToDegrees;
    private final float mCenterX;
    private final float mCenterY;
    private Camera mCamera;

    public Flip3DAnimation(final float fromDegrees, final float toDegrees, final float centerX, final float centerY) {
        super();
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
    }

    @Override
    public void initialize(final int width, final int height, final int parentWidth, final int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(final float interpolatedTime, final Transformation t) {
        final float fromDegrees = mFromDegrees;
        final float degrees = fromDegrees + (mToDegrees - fromDegrees) * interpolatedTime;

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();

        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}
