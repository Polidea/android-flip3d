package pl.polidea.androidflip3d;

import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * Stores state of the flip3D view. It is stored so that the view can actually
 * be detached from the information - and the views to be reused in classes like
 * gallery, list etc. - generally everywhere where views can be reused.
 * 
 */
public class Flip3DViewState {

    private static final String TAG = Flip3DViewState.class.getSimpleName();

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            targetViewIndex = ViewIndex.getTheOtherViewIndex(currentViewIndex);
            startRotationToTheOtherSide(true, true);
        }
    };

    private final int id;

    public Flip3DViewState(final int id) {
        this.id = id;
    }

    /**
     * Listener serving end of animation.
     * 
     */
    public class FinishFlipping implements AnimationListener {
        private final int newStateViewIndex;

        public FinishFlipping(final int targetViewIndex) {
            this.newStateViewIndex = targetViewIndex;
        }

        @Override
        public void onAnimationStart(final Animation animation) {
            // do nothing
        }

        @Override
        public void onAnimationEnd(final Animation animation) {
            oneSideFlippingEnded(newStateViewIndex);
        }

        @Override
        public void onAnimationRepeat(final Animation animation) {
            // do nothing
        }
    }

    /**
     * View attached to the state.
     */
    private Flip3DView view;

    /**
     * Listener to handle the flipping events.
     * 
     */
    public interface Flip3DViewListener {
        /**
         * Reported when flipping is started. Either on click or when forced
         * (and previously set on the other side). It will not be triggered when
         * forced and it is either on correct side or still flipping.
         * 
         * @param viewState
         *            viewstate originating the event
         * @param startingSide
         *            starting side of flipping (0 - FRONT, 1- BACK)
         * @param manuallyTriggered
         *            true when view was actually clicked and not forced to flip
         */
        void onStartedFlipping(Flip3DViewState viewState, int startingSide,
                boolean manuallyTriggered);

        /**
         * Reported when flipping is successfully finished. It will not be
         * triggered when flipping is being forced - so when we will flip back
         * immediately because we are coming back. It will also be called when
         * animation is canceled because view is moved out of the visible area.
         * 
         * @param viewState
         *            view originating the event
         * @param endingSide
         *            ending side of flipping (0 - FRONT, 1- BACK)
         * @param manuallyTriggered
         *            true when view was actually clicked and not forced to flip
         *            by some other means. Note that the manuallyTriggered
         *            status can be cancelled if the view is forced before it
         *            manages to flip
         */
        void onFinishedFlipping(Flip3DViewState viewState, int endingSide,
                boolean manuallyTriggered);
    }

    private int currentViewIndex = ViewIndex.FRONT_VIEW;

    private int targetViewIndex = ViewIndex.FRONT_VIEW;

    private boolean flipping = false;

    private boolean beingForced = false;

    private Flip3DViewListener flip3dViewListener = null;

    public Flip3DViewListener getFlip3dViewListener() {
        return flip3dViewListener;
    }

    public void setFlip3dViewListener(
            final Flip3DViewListener flip3dViewListener) {
        this.flip3dViewListener = flip3dViewListener;
    }

    public int getCurrentViewIndex() {
        return currentViewIndex;
    }

    public int getTargetViewIndex() {
        return targetViewIndex;
    }

    public boolean isFlipping() {
        return flipping;
    }

    public boolean isBeingForced() {
        return beingForced;
    }

    public void setCurrentViewIndex(final int currentViewIndex) {
        this.currentViewIndex = currentViewIndex;
    }

    public void setTargetViewIndex(final int targetViewIndex) {
        this.targetViewIndex = targetViewIndex;
    }

    public void setFlipping(final boolean flipping) {
        this.flipping = flipping;
        if (view != null) {
            view.setFlipping(flipping);
        }
    }

    public void setBeingForced(final boolean beingForced) {
        this.beingForced = beingForced;
    }

    public Flip3DView getView() {
        return view;
    }

    public synchronized void setView(final Flip3DView view) {
        if (this.view != null) {
            this.currentViewIndex = targetViewIndex;
            if (flipping) {
                setStateAfterFlippingFinished(!beingForced);
            }
        }
        this.targetViewIndex = currentViewIndex;
        this.flipping = false;
        this.beingForced = false;
        this.view = view;
        if (view != null) {
            view.setOnClickListener(clickListener);
            view.initializeViewState(currentViewIndex);
        }
    }

    /**
     * Force specified side of the view with animation (only does animation if
     * needed - if view is already in desired state, does nothing).
     * 
     * @param viewIndex
     *            index to which
     */
    public synchronized void forceFlipTo(final int viewIndex) {
        if (beingForced) {
            Log.d(TAG,
                    id + ": Already forced to "
                            + ViewIndex.getViewType(targetViewIndex));
            return;
        }
        Log.d(TAG, id + ": Forcing move to " + ViewIndex.getViewType(viewIndex));
        targetViewIndex = viewIndex;
        if (flipping) {
            Log.d(TAG,
                    id + ": Already flipping. Set target to "
                            + ViewIndex.getViewType(viewIndex));
            beingForced = true;
        } else if (currentViewIndex != targetViewIndex) {
            Log.d(TAG, id + ": Not flipping but need to flip back to "
                    + ViewIndex.getViewType(viewIndex));
            beingForced = true;
            startRotationToTheOtherSide(true, false);
        } else {
            Log.d(TAG,
                    id + ": Already in the right state: "
                            + ViewIndex.getViewType(viewIndex));
        }
    }

    /**
     * Animation or other means should use it in order to notify that flipping
     * has finished.
     * 
     * @param newStateIndex
     *            index to which we just flipped
     */
    private synchronized void oneSideFlippingEnded(final int newStateIndex) {
        Log.d(TAG,
                id + ": Ended flipping to "
                        + ViewIndex.getViewType(newStateIndex));
        currentViewIndex = newStateIndex;
        setFlipping(false);
        if (beingForced) {
            if (targetViewIndex == newStateIndex || view == null) {
                beingForced = false;
                setStateAfterFlippingFinished(false);
            } else {
                Log.d(TAG,
                        id + ": Flipping back, forcibly to "
                                + ViewIndex.getViewType(targetViewIndex));
                startRotationToTheOtherSide(false, false);
            }
        } else {
            setStateAfterFlippingFinished(true);
        }
    }

    private static String getMode(final boolean manuallyTriggered) {
        return manuallyTriggered ? "<Manual>" : "<Forced>";
    }

    /**
     * Set state after we know that flipping has actually been finished
     * (finally).
     * 
     * @param manuallyTriggered
     *            if the flip has been manually triggered
     */
    private synchronized void setStateAfterFlippingFinished(
            final boolean manuallyTriggered) {
        Log.d(TAG,
                id + ": " + getMode(manuallyTriggered)
                        + ": Flipping finished to "
                        + ViewIndex.getViewType(targetViewIndex));
        currentViewIndex = targetViewIndex;
        if (view != null) {
            view.requestViewIndexFocus(currentViewIndex);
            view.setViewClickability(currentViewIndex, true);
        }
        if (flip3dViewListener != null) {
            flip3dViewListener.onFinishedFlipping(this, currentViewIndex,
                    manuallyTriggered);
        }
    }

    private synchronized void startRotationToTheOtherSide(
            final boolean notifyListener, final boolean manuallyTriggered) {
        setFlipping(true);
        final int theOtherSide = ViewIndex
                .getTheOtherViewIndex(currentViewIndex);
        if (notifyListener && flip3dViewListener != null) {
            flip3dViewListener.onStartedFlipping(this, currentViewIndex,
                    manuallyTriggered);
        }
        if (view != null) {
            Log.d(TAG, "View is not null so setting clikability now");
            view.setViewClickability(currentViewIndex, false);
            view.setFinishFlippingListener(new FinishFlipping(theOtherSide));
            view.startRotation(currentViewIndex);
        } else {
            Log.d(TAG, "View is null so finishing immediately");
            oneSideFlippingEnded(ViewIndex
                    .getTheOtherViewIndex(targetViewIndex));
        }
    }

    /**
     * Sets listener to fliping events.
     * 
     * @param flip3dViewListener
     *            listener.
     */
    public synchronized void setFlip3DViewListener(
            final Flip3DViewListener flip3dViewListener) {
        this.flip3dViewListener = flip3dViewListener;
    }

    /**
     * Get Id of the control.
     * 
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Attaches or re-attaches view to the state.
     * 
     * @param i
     *            position
     * @param viewStates
     *            list of states
     * @param view
     *            view to attach
     * @param <State>
     *            state type
     * @return view state with the view attached - view should be set in it
     *         after preparing.
     */
    public static <State extends Flip3DViewState> State attachViewToViewState(
            final int i, final List<State> viewStates, final Flip3DView view) {
        final int oldViewId = view.getId();
        if (oldViewId >= 0 && oldViewId < viewStates.size()) {
            final State oldState = viewStates.get(oldViewId);
            oldState.setView(view); // detach view from old state
        }
        return viewStates.get(i);
    }

    /**
     * Detaches view in case it is owned by the state.
     * 
     * @param view
     *            view to detach
     */
    public synchronized void detachView(final Flip3DView view) {
        if (flipping) {
            view.clearAllAnimations();
        }
    }

}
