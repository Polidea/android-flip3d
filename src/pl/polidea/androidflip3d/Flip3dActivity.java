package pl.polidea.androidflip3d;

import pl.polidea.androidflip3d.Flip3DView.Flip3DViewListener;
import android.app.Activity;
import android.os.Bundle;

/**
 * Just a test activity to test the behaviour of the flip image.
 * 
 */
public class Flip3DActivity extends Activity {
    private Flip3DView firstView;
    private Flip3DView secondView;
    private Flip3DView thirdView;
    private Flip3DView fourthView;

    private class FirstViewListener implements Flip3DViewListener {
        @Override
        public void onStartedFlipping(final Flip3DView view,
                final int startingSide, final boolean clicked) {
            if (clicked) {
                secondView.forceMoveTo(ViewIndex.FRONT_VIEW);
            }
        }

        @Override
        public void onFinishedFlipping(final Flip3DView view,
                final int endingSide, final boolean clicked) {
            // do nothing
        }
    }

    private class SecondViewListener implements Flip3DViewListener {
        @Override
        public void onStartedFlipping(final Flip3DView view,
                final int startingSide, final boolean clicked) {
            if (clicked) {
                firstView.forceMoveTo(ViewIndex.FRONT_VIEW);
            }
        }

        @Override
        public void onFinishedFlipping(final Flip3DView view,
                final int endingSide, final boolean clicked) {
            // do nothing
        }
    }

    private class ThirdViewListener implements Flip3DViewListener {
        @Override
        public void onStartedFlipping(final Flip3DView view,
                final int startingSide, final boolean clicked) {
            // do nothing
        }

        @Override
        public void onFinishedFlipping(final Flip3DView view,
                final int endingSide, final boolean clicked) {
            if (clicked) {
                fourthView.forceMoveTo(ViewIndex.BACK_VIEW);
            }
        }
    }

    private class FourthViewListener implements Flip3DViewListener {
        @Override
        public void onStartedFlipping(final Flip3DView view,
                final int startingSide, final boolean clicked) {
            // do nothing
        }

        @Override
        public void onFinishedFlipping(final Flip3DView view,
                final int endingSide, final boolean clicked) {
            if (clicked) {
                thirdView.forceMoveTo(ViewIndex.BACK_VIEW);
            }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        firstView = (Flip3DView) findViewById(R.id.firstView);
        firstView.setFlip3DViewListener(new FirstViewListener());
        secondView = (Flip3DView) findViewById(R.id.secondView);
        secondView.setFlip3DViewListener(new SecondViewListener());
        thirdView = (Flip3DView) findViewById(R.id.thirdView);
        thirdView.setFlip3DViewListener(new ThirdViewListener());
        fourthView = (Flip3DView) findViewById(R.id.fourthView);
        fourthView.setFlip3DViewListener(new FourthViewListener());
    }
}
