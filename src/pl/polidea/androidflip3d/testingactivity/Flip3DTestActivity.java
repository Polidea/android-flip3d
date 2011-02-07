package pl.polidea.androidflip3d.testingactivity;

import pl.polidea.androidflip3d.Flip3DView;
import pl.polidea.androidflip3d.Flip3DViewState;
import pl.polidea.androidflip3d.Flip3DViewState.Flip3DViewListener;
import pl.polidea.androidflip3d.R;
import pl.polidea.androidflip3d.ViewIndex;
import android.app.Activity;
import android.os.Bundle;

/**
 * Just a test activity to test the behaviour of the flip image.
 * 
 */
public class Flip3DTestActivity extends Activity {
    private static final String PACKAGE_NAME = "pl.polidea.androidflip3d";
    private static final int NUM_VIEWS = 4;
    private final Flip3DViewState[] viewStates = new Flip3DViewState[NUM_VIEWS];

    private final Flip3DViewListener[] listeners = { new Flip3DViewListener() {
        @Override
        public void onStartedFlipping(final Flip3DViewState view, final int startingSide,
                final boolean manuallyTriggered) {
            if (manuallyTriggered && startingSide == ViewIndex.FRONT_VIEW) {
                viewStates[1].forceFlipTo(ViewIndex.FRONT_VIEW);
            }
        }

        @Override
        public void onFinishedFlipping(final Flip3DViewState view, final int endingSide, final boolean manuallyTriggered) {
            // do nothing
        }
    }, new Flip3DViewListener() {
        @Override
        public void onStartedFlipping(final Flip3DViewState view, final int startingSide,
                final boolean manuallyTriggered) {
            if (manuallyTriggered) {
                viewStates[0].forceFlipTo(ViewIndex.FRONT_VIEW);
            }
        }

        @Override
        public void onFinishedFlipping(final Flip3DViewState view, final int endingSide, final boolean manuallyTriggered) {
            // do nothing
        }
    }, new Flip3DViewListener() {
        @Override
        public void onStartedFlipping(final Flip3DViewState view, final int startingSide,
                final boolean manuallyTriggered) {
            // do nothing
        }

        @Override
        public void onFinishedFlipping(final Flip3DViewState view, final int endingSide, final boolean manuallyTriggered) {
            if (manuallyTriggered && endingSide == ViewIndex.FRONT_VIEW) {
                viewStates[3].forceFlipTo(ViewIndex.BACK_VIEW);
            }
        }
    }, new Flip3DViewListener() {

        @Override
        public void onStartedFlipping(final Flip3DViewState view, final int startingSide,
                final boolean manuallyTriggered) {
            // do nothing
        }

        @Override
        public void onFinishedFlipping(final Flip3DViewState view, final int endingSide, final boolean manuallyTriggered) {
            if (manuallyTriggered) {
                viewStates[2].forceFlipTo(ViewIndex.BACK_VIEW);
            }
        }
    } };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // ids retrieved dynamically -> avoid errors when importing as library
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Flip3DView[] views = new Flip3DView[NUM_VIEWS];
        for (int i = 0; i < NUM_VIEWS; i++) {
            views[i] = (Flip3DView) findViewById(this.getResources().getIdentifier("view" + i, "id", PACKAGE_NAME));
            viewStates[i] = new Flip3DViewState(i);
            viewStates[i].setView(views[i]);
            viewStates[i].setFlip3dViewListener(listeners[i]);

        }
    }
}
