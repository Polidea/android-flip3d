package pl.polidea.androidflip3d.testingactivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.polidea.androidflip3d.AbstractGridFlip3DImageAdapter;
import pl.polidea.androidflip3d.Flip3DView;
import pl.polidea.androidflip3d.Flip3DViewState;
import pl.polidea.androidflip3d.Flip3DViewState.Flip3DViewListener;
import pl.polidea.androidflip3d.ViewIndex;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

/**
 * Tests Flip3D in grid.
 * 
 */
public class TestGridAdapter extends
        AbstractGridFlip3DImageAdapter<TestFlip3DViewState, Flip3DView> {
    private static final String TAG = TestGridAdapter.class.getSimpleName();
    private static final int MAX = 300;
    private final List<TestFlip3DViewState> states = new ArrayList<TestFlip3DViewState>(
            MAX);
    private final Context context;
    private final int imageWidth;

    public TestGridAdapter(final Context context, final int imageWidth) {
        super(imageWidth);
        this.context = context;
        this.imageWidth = imageWidth;
        for (int i = 0; i < MAX; i++) {
            final Random r = new Random();
            final byte[] b = new byte[4];
            r.nextBytes(b);
            final int color = Color.argb(b[0], b[1], b[2], b[3]);
            final TestFlip3DViewState state = new TestFlip3DViewState(i, color);
            states.add(state);
            setViewStates(states);
        }

    }

    @Override
    protected Flip3DView createView() {
        return new Flip3DView(context);
    }

    @Override
    protected void prepareView(final int position, final Flip3DView view) {
        final TestFlip3DViewState newState = Flip3DViewState
                .attachViewToViewState(position, states, view);
        view.setInternalPadding(0);
        view.setInternalMargin((imageWidth * 0));
        final int res = context.getResources().getIdentifier("icon",
                "drawable", "pl.polidea.androidflip3d");
        view.setImageBackDrawable(context.getResources().getDrawable(res));
        final int color = getViewStates().get(position).getColor();
        view.setImageFrontDrawable(new ColorDrawable(color));
        newState.setFlip3dViewListener(new Flip3DViewListener() {
            @Override
            public void onStartedFlipping(final Flip3DViewState viewState,
                    final int startingSide, final boolean manuallyTriggered) {

                if (manuallyTriggered) {
                    Log.d(TAG, "Started flipping view " + view.getId());
                    for (final Flip3DViewState flipViewState : getViewStates()) {
                        if (position != flipViewState.getId()) {
                            Log.v(TAG, "Forcing view " + flipViewState.getId()
                                    + " to front");
                            flipViewState.forceFlipTo(ViewIndex.FRONT_VIEW);
                        }
                    }
                }
            }

            @Override
            public void onFinishedFlipping(final Flip3DViewState viewState,
                    final int endingSide, final boolean manuallyTriggered) {
                // we do nothing
            }
        });

        newState.setView(view);

    }
}
