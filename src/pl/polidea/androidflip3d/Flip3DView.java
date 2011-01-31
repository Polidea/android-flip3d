package pl.polidea.androidflip3d;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * Views that can be swapped on click in 3D animation mode.
 * 
 */
public class Flip3DView extends FrameLayout {

    private static final String TAG = Flip3DView.class.getSimpleName();

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
         * @param view
         *            view originating the event
         * @param startingSide
         *            starting side of flipping (0 - FRONT, 1- BACK)
         * @param clicked
         *            true when view was actually clicked and not forced to flip
         */
        void onStartedFlipping(Flip3DView view, int startingSide, boolean clicked);

        /**
         * Reported when flipping is successfully finished. It will not be
         * triggered when flipping is being forced - so when we will flip back
         * immediately because we are coming back.
         * 
         * @param view
         *            view originating the event
         * @param endingSide
         *            ending side of flipping (0 - FRONT, 1- BACK)
         * @param clicked
         *            true when view was actually clicked and not forced to flip
         */
        void onFinishedFlipping(Flip3DView view, int endingSide, boolean clicked);
    }

    private static final int DEFAULT_ANIMATION_LENGTH = 500;
    private static final int DEFAULT_INTERNAL_PADDING = 0;

    private static final int DEFAULT_FRONT_TO_BACK = RotationDirection.ROTATE_LEFT;
    private static final int DEFAULT_BACK_TO_FRONT = RotationDirection.ROTATE_RIGHT;

    private final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,
            LayoutParams.FILL_PARENT);

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            Log.d(TAG, "Click executed on view: " + ViewIndex.getViewType(view.getId()));
            startRotationToTheOtherSide(true, true);
        }
    };

    private final View.OnClickListener clickHidingListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            Log.d(TAG, "Click ignored on view: " + ViewIndex.getViewType(view.getId()));
        }
    };

    /**
     * Listener serving end of animation.
     * 
     */
    public class FinishFlipping implements AnimationListener {
        private final int targetViewIndex;

        public FinishFlipping(final int targetViewIndex) {
            this.targetViewIndex = targetViewIndex;
        }

        @Override
        public void onAnimationStart(final Animation animation) {
            // do nothing
        }

        @Override
        public void onAnimationEnd(final Animation animation) {
            finishFlipping(targetViewIndex);
        }

        @Override
        public void onAnimationRepeat(final Animation animation) {
            // do nothing
        }
    }

    private final View[] views = new View[ViewIndex.VIEW_NUMBER];

    private int currentViewIndex = ViewIndex.FRONT_VIEW;
    private int internalPadding = DEFAULT_INTERNAL_PADDING;
    private long animationLength = DEFAULT_ANIMATION_LENGTH;
    private int frontToBack = DEFAULT_FRONT_TO_BACK;
    private int backToFront = DEFAULT_BACK_TO_FRONT;

    private boolean flipping = false;
    private boolean beingForced;
    private int forcedIndex;
    private Flip3DViewListener flip3dViewListener = null;
    private Handler handler;

    /**
     * Sets amount of internal padding.
     * 
     * @param internalPadding
     *            internal padding in pixels
     */
    public void setInternalPadding(final int internalPadding) {
        this.internalPadding = internalPadding;
    }

    /**
     * Sets length of animation flipping.
     * 
     * @param animationLength
     *            length of animation in milliseconds
     */
    public synchronized void setAnimationLength(final long animationLength) {
        this.animationLength = animationLength;
    }

    public Flip3DView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Flip3DView);
        try {
            parsePaddingAttributes(a);
            initializeViews();
            parseImageAttributes(a);
            parseOtherAttributes(a);
        } finally {
            a.recycle();
        }
    }

    public Flip3DView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Flip3DView(final Context context) {
        super(context);
        initializeViews();
        handler = new Handler();
    }

    private void parsePaddingAttributes(final TypedArray a) {
        internalPadding = a.getDimensionPixelSize(R.styleable.Flip3DView_internal_padding, DEFAULT_INTERNAL_PADDING);
    }

    private void parseImageAttributes(final TypedArray a) {
        final Drawable front = a.getDrawable(R.styleable.Flip3DView_src_front);
        if (front != null) {
            setImageFrontDrawable(front);
        }
        final Drawable back = a.getDrawable(R.styleable.Flip3DView_src_back);
        if (back != null) {
            setImageBackDrawable(back);
        }
    }

    private synchronized void parseOtherAttributes(final TypedArray a) {
        animationLength = a.getInt(R.styleable.Flip3DView_animation_length_millis, DEFAULT_ANIMATION_LENGTH);
        frontToBack = a.getInt(R.styleable.Flip3DView_front_to_back_flip_direction, DEFAULT_FRONT_TO_BACK);
        backToFront = a.getInt(R.styleable.Flip3DView_back_to_front_flip_direction, DEFAULT_BACK_TO_FRONT);
    }

    /**
     * Sets rotation mode for front to back change.
     * 
     * <pre>
     * Accepted values are:
     *  0 - LEFT
     *  1 - RIGHT
     * </pre>
     * 
     * @param direction
     *            rotation direction
     */
    public synchronized void setFrontToBack(final int direction) {
        this.frontToBack = direction;
    }

    /**
     * Sets rotation mode for back to front change.
     * 
     * <pre>
     * Accepted values are:
     *  0 - LEFT
     *  1 - RIGHT
     * </pre>
     * 
     * @param direction
     *            rotation direction
     */
    public synchronized void setBackToFront(final int direction) {
        this.backToFront = direction;
    }

    /**
     * Sets layout parameters of the image view depending on the type of the
     * Drawable.
     * 
     * @param view
     *            the image view
     */
    private void setViewParameters(final View view) {
        view.setLayoutParams(layoutParams);
    }

    /**
     * Sets layout parameters of the image view depending on the type of the
     * Drawable.
     * 
     * @param imageView
     *            the image view
     * @param drawable
     *            drawable to set
     */
    private void setImageParameters(final ImageView imageView, final Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    /**
     * Sets view to the given side.
     * 
     * @param viewSide
     *            side of the view
     * @param view
     *            view to set
     */
    private void setView(final int viewSide, final View view) {
        if (this.views[viewSide] != null) {
            this.removeView(this.views[viewSide]);
        }
        this.views[viewSide] = view;
        view.setId(viewSide);
        this.addView(view);
    }

    /**
     * Sets front view on the control.
     * 
     * @param viewFront
     *            front view
     */
    public final void setViewFront(final View viewFront) {
        setInternalView(ViewIndex.FRONT_VIEW, viewFront);
    }

    /**
     * Sets back view on the control.
     * 
     * @param viewBack
     *            back view
     */
    public final void setViewBack(final View viewBack) {
        setInternalView(ViewIndex.BACK_VIEW, viewBack);
    }

    private void setInternalView(final int viewSide, final View view) {
        final FrameLayout frame = (FrameLayout) inflate(getContext(), R.layout.view_layout_with_padding, null);
        frame.setPadding(internalPadding, internalPadding, internalPadding, internalPadding);
        setViewParameters(view);
        frame.addView(view);
        setView(viewSide, frame);
    }

    private void setImageDrawable(final int viewSide, final Drawable drawable) {
        final FrameLayout frame = (FrameLayout) inflate(getContext(), R.layout.image_layout_with_padding, null);
        frame.setPadding(internalPadding, internalPadding, internalPadding, internalPadding);
        final ImageView imageView = (ImageView) frame.findViewById(R.id.padded_view);
        imageView.setScaleType(ScaleType.CENTER_INSIDE);
        setImageParameters(imageView, drawable);
        setView(viewSide, frame);
    }

    /**
     * Sets image drawable for front.
     * 
     * @param drawable
     *            drawable for front.
     */
    public final void setImageFrontDrawable(final Drawable drawable) {
        setImageDrawable(ViewIndex.FRONT_VIEW, drawable);
    }

    /**
     * Sets image drawable for back.
     * 
     * @param drawable
     *            drawable for back.
     */
    public final void setImageBackDrawable(final Drawable drawable) {
        setImageDrawable(ViewIndex.BACK_VIEW, drawable);
    }

    /**
     * Sets image drawable for background.
     * 
     * @param drawable
     *            drawable for background.
     */
    private void setImageBackgroundDrawable(final Drawable drawable) {
        setImageDrawable(ViewIndex.BACKGROUND_VIEW, drawable);
    }

    /**
     * Sets image drawable for foreground (not add it to the main view).
     * 
     * @param drawable
     *            drawable for foreground.
     */
    private void setImageForegroundDrawable(final Drawable drawable) {
        setImageDrawable(ViewIndex.FOREGROUND_VIEW, drawable);
    }

    private void initializeViews() {
        setImageBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setImageFrontDrawable(new ColorDrawable(Color.BLUE));
        setImageBackDrawable(new ColorDrawable(Color.RED));
        setImageForegroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private synchronized void initializeViewState() {
        views[ViewIndex.BACKGROUND_VIEW].setVisibility(View.VISIBLE);
        views[ViewIndex.BACKGROUND_VIEW].setOnClickListener(clickListener);
        setViewClickability(views[ViewIndex.BACKGROUND_VIEW], true);
        views[ViewIndex.FRONT_VIEW].setVisibility(View.VISIBLE);
        setViewClickability(views[ViewIndex.FRONT_VIEW], true);
        views[ViewIndex.BACK_VIEW].setVisibility(View.INVISIBLE);
        setViewClickability(views[ViewIndex.BACK_VIEW], false);
        views[ViewIndex.FOREGROUND_VIEW].setVisibility(View.INVISIBLE);
        setViewClickability(views[ViewIndex.FOREGROUND_VIEW], true);
    }

    private void setViewClickability(final View view, final boolean enable) {
        view.setClickable(true);
        if (enable) {
            if (view.getId() == ViewIndex.FOREGROUND_VIEW) {
                // always ignore clicks on foreground view
                view.setOnClickListener(clickHidingListener);
            } else {
                view.setOnClickListener(clickListener);
            }
            view.bringToFront();
            view.requestFocus();
        } else {
            view.setOnClickListener(clickHidingListener);
        }
    }

    /**
     * Starts rotation according to direction.
     * 
     * @param direction
     *            direction (0 - LEFT, 1 - RIGHT)
     */
    private synchronized void startRotation(final int direction) {
        setFlipping(true);
        final float centerX = getWidth() / 2.0f;
        final float centerY = getHeight() / 2.0f;
        final Flip3DAnimation rotation = new Flip3DAnimation(0, RotationDirection.getMultiplier(direction) * 90,
                centerX, centerY);
        rotation.setDuration(animationLength);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new GetToTheMiddleOfFlipping(currentViewIndex, views, animationLength, direction,
                new FinishFlipping(ViewIndex.getTheOtherViewIndex(currentViewIndex))));
        views[currentViewIndex].startAnimation(rotation);
    }

    @Override
    protected void onAttachedToWindow() {
        initializeViewState();
    }

    private synchronized void setFlipping(final boolean flipping) {
        final View foregroundView = views[ViewIndex.FOREGROUND_VIEW];
        if (flipping) {
            // make sure the view is taking over all the clicks
            foregroundView.setVisibility(VISIBLE);
            foregroundView.bringToFront();
            setViewClickability(foregroundView, true);
        } else {
            setViewClickability(foregroundView, false);
            foregroundView.setVisibility(GONE);
        }
        this.flipping = flipping;
    }

    /**
     * Force specified side of the view with animation if needed.
     * 
     * @param viewIndex
     *            index of view to force
     */
    public synchronized void forceMoveTo(final int viewIndex) {
        if (beingForced) {
            return;
        }
        if (flipping) {
            beingForced = true;
            forcedIndex = viewIndex;
        } else if (viewIndex != currentViewIndex) {
            beingForced = true;
            forcedIndex = viewIndex;
            startRotationToTheOtherSide(true, false);
        }
    }

    private synchronized void finishFlipping(final int targetViewIndex) {
        setFlipping(false);
        currentViewIndex = targetViewIndex;
        if (beingForced) {
            if (currentViewIndex == forcedIndex) {
                beingForced = false;
                setViewClickability(views[currentViewIndex], true);
                if (flip3dViewListener != null) {
                    flip3dViewListener.onFinishedFlipping(Flip3DView.this, currentViewIndex, false);
                }
            } else {
                startRotationToTheOtherSide(false, false);
            }
        } else {
            views[currentViewIndex].requestFocus();
            setViewClickability(views[currentViewIndex], true);
            if (flip3dViewListener != null) {
                flip3dViewListener.onFinishedFlipping(Flip3DView.this, currentViewIndex, true);
            }
        }
    }

    private synchronized void startRotationToTheOtherSide(final boolean notifyListener, final boolean clicked) {
        setViewClickability(views[currentViewIndex], false);
        if (currentViewIndex == ViewIndex.FRONT_VIEW) {
            startRotation(frontToBack);
        } else {
            startRotation(backToFront);
        }
        if (notifyListener && flip3dViewListener != null) {
            flip3dViewListener.onStartedFlipping(Flip3DView.this, currentViewIndex, clicked);
        }
    }

    public synchronized void setFlip3DViewListener(final Flip3DViewListener flip3dViewListener) {
        this.flip3dViewListener = flip3dViewListener;
    }
}
