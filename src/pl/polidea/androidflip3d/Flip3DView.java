package pl.polidea.androidflip3d;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

/**
 * Views that can be swapped on click in 3D animation mode.
 * 
 */
public class Flip3DView extends FrameLayout {

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
        void onStartedFlipping(Flip3DView view, int startingSide,
                boolean clicked);

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

    private final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            startRotationToTheOtherSide(true, true);
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

    private final View[] views = new View[2];

    private int currentViewIndex = ViewIndex.FRONT_VIEW;
    private int internalPadding = DEFAULT_INTERNAL_PADDING;
    private long animationLength = DEFAULT_ANIMATION_LENGTH;
    private int frontToBack = DEFAULT_FRONT_TO_BACK;
    private int backToFront = DEFAULT_BACK_TO_FRONT;

    private boolean flipping = false;
    private boolean beingForced;
    private int forcedIndex;
    private Flip3DViewListener flip3dViewListener = null;

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
    public void setAnimationLength(final long animationLength) {
        this.animationLength = animationLength;
    }

    public Flip3DView(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Flip3DView);
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
    }

    private void parsePaddingAttributes(final TypedArray a) {
        internalPadding = a.getDimensionPixelSize(
                R.styleable.Flip3DView_internal_padding,
                DEFAULT_INTERNAL_PADDING);
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

    private void parseOtherAttributes(final TypedArray a) {
        animationLength = a.getInt(
                R.styleable.Flip3DView_animation_length_millis,
                DEFAULT_ANIMATION_LENGTH);
        frontToBack = a.getInt(
                R.styleable.Flip3DView_front_to_back_flip_direction,
                DEFAULT_FRONT_TO_BACK);
        backToFront = a.getInt(
                R.styleable.Flip3DView_back_to_front_flip_direction,
                DEFAULT_BACK_TO_FRONT);
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
     * @param backToFront
     *            rotation direction
     */
    public void setFrontToBack(final int frontToBack) {
        this.frontToBack = frontToBack;
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
     * @param backToFront
     *            rotation direction
     */
    public void setBackToFront(final int backToFront) {
        this.backToFront = backToFront;
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
    private void setImageParameters(final ImageView imageView,
            final Drawable drawable) {
        imageView.setLayoutParams(layoutParams);
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
    private final void setView(final int viewSide, final View view) {
        if (this.views[viewSide] != null) {
            this.removeView(this.views[viewSide]);
        }
        this.views[viewSide] = view;
        this.addView(view);
    }

    /**
     * Sets front view on the control.
     * 
     * @param viewFront
     *            front view
     */
    public final void setViewFront(final View viewFront) {
        setView(ViewIndex.FRONT_VIEW, viewFront);
    }

    /**
     * Sets back view on the control.
     * 
     * @param viewBack
     *            back view
     */
    public final void setViewBack(final View viewBack) {
        setView(ViewIndex.BACK_VIEW, viewBack);
    }

    private final void setImageDrawable(final int viewSide,
            final Drawable drawable) {
        final LinearLayout v = (LinearLayout) inflate(getContext(),
                R.layout.image_layout_with_padding, null);
        v.setPadding(internalPadding, internalPadding, internalPadding,
                internalPadding);
        final ImageView imageView = (ImageView) v
                .findViewById(R.id.padded_view);
        imageView.setScaleType(ScaleType.FIT_XY);
        setImageParameters(imageView, drawable);
        setView(viewSide, v);
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

    private void initializeViews() {
        setImageFrontDrawable(new ColorDrawable(Color.BLUE));
        setImageBackDrawable(new ColorDrawable(Color.RED));
    }

    private void initializeViewState() {
        final int theOtherViewIndex = ViewIndex
                .getTheOtherViewIndex(currentViewIndex);
        views[currentViewIndex].setVisibility(View.VISIBLE);
        views[currentViewIndex].setOnClickListener(clickListener);
        views[currentViewIndex].setClickable(true);
        views[theOtherViewIndex].setVisibility(View.INVISIBLE);
        views[theOtherViewIndex].setOnClickListener(null);
        views[theOtherViewIndex].setClickable(false);
    }

    /**
     * Applies rotation according to direction
     * 
     * @param direction
     *            direction (0 - LEFT, 1 - RIGHT)
     */
    private synchronized void startRotation(final int direction) {
        setFlipping(true);
        final float centerX = views[currentViewIndex].getWidth() / 2.0f;
        final float centerY = views[currentViewIndex].getHeight() / 2.0f;
        final Flip3DAnimation rotation = new Flip3DAnimation(0,
                RotationDirection.getMultiplier(direction) * 90, centerX,
                centerY);
        rotation.setDuration(animationLength);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new GetToTheMiddleOfFlipping(
                currentViewIndex, views, animationLength, direction,
                new FinishFlipping(ViewIndex
                        .getTheOtherViewIndex(currentViewIndex))));
        views[currentViewIndex].startAnimation(rotation);
    }

    @Override
    protected void onAttachedToWindow() {
        initializeViewState();
    }

    private synchronized void setFlipping(final boolean flipping) {
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
            if (currentViewIndex != forcedIndex) {
                startRotationToTheOtherSide(false, false);
            } else {
                beingForced = false;
                views[currentViewIndex].requestFocus();
                views[currentViewIndex].setOnClickListener(clickListener);
                views[currentViewIndex].setClickable(true);
                if (flip3dViewListener != null) {
                    flip3dViewListener.onFinishedFlipping(Flip3DView.this,
                            currentViewIndex, false);
                }
            }
        } else {
            views[currentViewIndex].requestFocus();
            views[currentViewIndex].setClickable(true);
            views[currentViewIndex].setOnClickListener(clickListener);
            if (flip3dViewListener != null) {
                flip3dViewListener.onFinishedFlipping(Flip3DView.this,
                        currentViewIndex, true);
            }
        }
    }

    private synchronized void startRotationToTheOtherSide(
            final boolean notifyListener, final boolean clicked) {
        views[currentViewIndex].setOnClickListener(null);
        views[currentViewIndex].setClickable(false);
        if (currentViewIndex == ViewIndex.FRONT_VIEW) {
            startRotation(frontToBack);
        } else {
            startRotation(backToFront);
        }
        if (notifyListener && flip3dViewListener != null) {
            flip3dViewListener.onStartedFlipping(Flip3DView.this,
                    currentViewIndex, clicked);
        }
    }

    public void setFlip3DViewListener(
            final Flip3DViewListener flip3dViewListener) {
        this.flip3dViewListener = flip3dViewListener;
    }
}
