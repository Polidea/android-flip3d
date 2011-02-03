package pl.polidea.androidflip3d;

import java.util.Arrays;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
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

    private static final int DEFAULT_ANIMATION_LENGTH = 500;
    private static final int DEFAULT_INTERNAL_PADDING = 0;
    private static final int DEFAULT_INTERNAL_MARGIN = 0;

    private static final int DEFAULT_FRONT_TO_BACK = RotationDirection.ROTATE_LEFT;
    private static final int DEFAULT_BACK_TO_FRONT = RotationDirection.ROTATE_RIGHT;

    private final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
            LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

    private final View.OnClickListener clickHidingListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            Log.d(TAG, "Click ignored: " + ViewIndex.getViewType(view.getId()));
        }
    };

    private AnimationListener finishFlippingListener;

    private final View[] views = new View[ViewIndex.VIEW_NUMBER];
    private int internalPadding = DEFAULT_INTERNAL_PADDING;
    private long animationLength = DEFAULT_ANIMATION_LENGTH;
    private int frontToBack = DEFAULT_FRONT_TO_BACK;
    private int backToFront = DEFAULT_BACK_TO_FRONT;
    private int internalMargin = DEFAULT_INTERNAL_MARGIN;

    private final OnClickListener listenerDelegate = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            Log.v(TAG, "Delegate clicked on view " + v.getId() + ", view:" + v);
            if (listener != null) {
                Log.v(TAG, "Finding the listener.");
                listener.onClick(v);
            }
        }
    };

    private OnClickListener listener;

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
     * Sets amount of internal margin.
     * 
     * @param internalMargin
     *            internal padding in pixels
     */
    public void setInternalMargin(final int internalMargin) {
        this.internalMargin = internalMargin;
    }

    /**
     * Sets length of animation flippingx.
     * 
     * @param animationLength
     *            length of animation in milliseconds
     */
    public synchronized void setAnimationLength(final long animationLength) {
        this.animationLength = animationLength;
    }

    public Flip3DView(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
        setId(-1);
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
        setId(-1);
        initializeViews();
    }

    private void parsePaddingAttributes(final TypedArray a) {
        internalPadding = a.getDimensionPixelSize(
                R.styleable.Flip3DView_internal_padding,
                DEFAULT_INTERNAL_PADDING);
        internalMargin = a
                .getDimensionPixelSize(R.styleable.Flip3DView_internal_margin,
                        DEFAULT_INTERNAL_MARGIN);
        layoutParams.setMargins(internalMargin, internalMargin, internalMargin,
                internalMargin);
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
    private void setImageParameters(final ImageView imageView,
            final Drawable drawable) {
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
    private void setView(final int viewSide, final FrameLayout view) {
        if (this.views[viewSide] != null) {
            this.removeView(this.views[viewSide]);
        }
        this.views[viewSide] = view;
        view.setId(viewSide);
        setViewParameters(view);
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
        final FrameLayout frame = (FrameLayout) inflate(getContext(),
                R.layout.view_layout_with_padding, null);
        frame.setPadding(internalPadding, internalPadding, internalPadding,
                internalPadding);
        frame.addView(view);
        setView(viewSide, frame);
    }

    private void setImageDrawable(final int viewSide, final Drawable drawable) {
        final FrameLayout frame = (FrameLayout) inflate(getContext(),
                R.layout.image_layout_with_padding, null);
        frame.setPadding(internalPadding, internalPadding, internalPadding,
                internalPadding);
        final ImageView imageView = (ImageView) frame
                .findViewById(R.id.padded_view);
        imageView.setScaleType(ScaleType.FIT_CENTER);
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
     * Sets image drawable for foreground (not add it to the main view).
     * 
     * @param drawable
     *            drawable for foreground.
     */
    private void setImageForegroundDrawable(final Drawable drawable) {
        setImageDrawable(ViewIndex.FOREGROUND_VIEW, drawable);
    }

    private void initializeViews() {
        setImageFrontDrawable(new ColorDrawable(Color.BLUE));
        setImageBackDrawable(new ColorDrawable(Color.RED));
        setImageForegroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    protected synchronized void initializeViewState(final int currentViewIndex) {
        views[ViewIndex.FOREGROUND_VIEW].setVisibility(View.INVISIBLE);
        setViewClickability(ViewIndex.FOREGROUND_VIEW, false);
        views[ViewIndex.BACK_VIEW]
                .setVisibility(currentViewIndex == ViewIndex.BACK_VIEW ? View.VISIBLE
                        : View.INVISIBLE);
        setViewClickability(ViewIndex.BACK_VIEW,
                currentViewIndex == ViewIndex.BACK_VIEW);
        views[ViewIndex.FRONT_VIEW]
                .setVisibility(currentViewIndex == ViewIndex.FRONT_VIEW ? View.VISIBLE
                        : View.INVISIBLE);
        setViewClickability(ViewIndex.FRONT_VIEW,
                currentViewIndex == ViewIndex.FRONT_VIEW);

    }

    public void setViewClickability(final int viewIndex, final boolean enable) {
        Log.v(TAG, "Setting view clickability for view " + getId() + " "
                + ViewIndex.getViewType(viewIndex) + " to " + enable);
        final View view = views[viewIndex];
        view.setClickable(true);
        if (enable) {
            if (viewIndex == ViewIndex.FOREGROUND_VIEW) {
                // always ignore clicks on foreground view
                view.setOnClickListener(clickHidingListener);
            } else {
                view.setOnClickListener(listenerDelegate);
            }
            view.bringToFront();
            view.requestFocus();
        } else {
            view.setOnClickListener(clickHidingListener);
        }
    }

    public void requestViewIndexFocus(final int viewIndex) {
        views[viewIndex].requestFocus();
    }

    /**
     * Starts rotation according to direction.
     * 
     * @param currentViewIndex
     *            starting index of view which to animate
     */
    public synchronized void startRotation(final int currentViewIndex) {
        Log.v(TAG,
                "Starting rotation from "
                        + ViewIndex.getViewType(currentViewIndex));
        final int direction = currentViewIndex == ViewIndex.FRONT_VIEW ? frontToBack
                : backToFront;
        setFlipping(true);
        final float centerX = getWidth() / 2.0f;
        final float centerY = getHeight() / 2.0f;
        final Flip3DAnimation rotation = new Flip3DAnimation(0,
                RotationDirection.getMultiplier(direction) * 90, centerX,
                centerY);
        rotation.setDuration(animationLength);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new GetToTheMiddleOfFlipping(
                currentViewIndex, views, animationLength, direction,
                finishFlippingListener));
        views[currentViewIndex].startAnimation(rotation);
        Log.v(TAG, " View: " + views[currentViewIndex] + ",Parent: "
                + views[currentViewIndex].getParent() + " View grandParent "
                + views[currentViewIndex].getParent().getParent());
        Log.v(TAG, "Animation started in " + currentViewIndex + " on view "
                + views[currentViewIndex]);
    }

    public void setFlipping(final boolean flipping) {
        final View foregroundView = views[ViewIndex.FOREGROUND_VIEW];
        if (flipping) {
            // make sure the view is taking over all the clicks
            setViewClickability(ViewIndex.FOREGROUND_VIEW, true);
            foregroundView.setVisibility(VISIBLE);
            foregroundView.bringToFront();
        } else {
            setViewClickability(ViewIndex.FOREGROUND_VIEW, false);
            foregroundView.setVisibility(GONE);
        }
    }

    @Override
    public void setOnClickListener(final OnClickListener l) {
        Log.v(TAG, "Setting the listener to " + l + " on view " + getId());
        super.setOnClickListener(l);
        this.listener = l;
    }

    /**
     * Listener to listen for flipping finished.
     * 
     * @param finishFlippingListener
     *            listener to listen to finish flipping
     */
    public void setFinishFlippingListener(
            final AnimationListener finishFlippingListener) {
        this.finishFlippingListener = finishFlippingListener;
    }

    @Override
    public String toString() {
        return "Flip3DView [layoutParams=" + layoutParams
                + ", clickHidingListener=" + clickHidingListener
                + ", finishFlippingListener=" + finishFlippingListener
                + ", views=" + Arrays.toString(views) + ", internalPadding="
                + internalPadding + ", animationLength=" + animationLength
                + ", frontToBack=" + frontToBack + ", backToFront="
                + backToFront + ", listenerDelegate=" + listenerDelegate
                + ", listener=" + listener + "]";
    }
}
