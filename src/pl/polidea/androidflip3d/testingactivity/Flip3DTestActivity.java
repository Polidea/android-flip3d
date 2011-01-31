package pl.polidea.androidflip3d.testingactivity;

import pl.polidea.androidflip3d.Flip3DView;
import pl.polidea.androidflip3d.Flip3DView.Flip3DViewListener;
import pl.polidea.androidflip3d.ViewIndex;
import android.app.Activity;
import android.os.Bundle;

/**
 * Just a test activity to test the behaviour of the flip image.
 * 
 */
public class Flip3DTestActivity extends Activity {
    private static final String PACKAGE_NAME = "pl.polidea.androidflip3d";
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
        // ids retrieved dynamically -> avoid errors when importing as library
        super.onCreate(savedInstanceState);
        setContentView(this.getResources().getIdentifier("main", "layout",
                "pl.polidea.androidflip3d"));
        firstView = (Flip3DView) findViewById(this.getResources()
                .getIdentifier("firstView", "id", PACKAGE_NAME));
        firstView.setFlip3DViewListener(new FirstViewListener());
        secondView = (Flip3DView) findViewById(this.getResources()
                .getIdentifier("secondView", "id", PACKAGE_NAME));
        secondView.setFlip3DViewListener(new SecondViewListener());
        thirdView = (Flip3DView) findViewById(this.getResources()
                .getIdentifier("thirdView", "id", PACKAGE_NAME));
        thirdView.setFlip3DViewListener(new ThirdViewListener());
        fourthView = (Flip3DView) findViewById(this.getResources()
                .getIdentifier("fourthView", "id", PACKAGE_NAME));
        fourthView.setFlip3DViewListener(new FourthViewListener());
    }
}
