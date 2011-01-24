package pl.polidea.androidflip3d;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Views that can be swapped on click in 3D animation mode.
 * 
 */
public class Flip3DView extends FrameLayout {

    private final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    final LinearLayout.LayoutParams colorLayoutParams = new LinearLayout.LayoutParams(
            LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            view.setClickable(false);
            if (isFirstView) {
                applyRotation(0, -90);
                isFirstView = false;
            } else {
                applyRotation(0, 90);
                isFirstView = true;
            }
        }
    }

    private View viewFront;
    private View viewBack;

    private boolean isFirstView = true;
    private int internalPadding;

    public void setInternalPadding(final int internalPadding) {
        this.internalPadding = internalPadding;
    }

    public Flip3DView(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
        parsePaddingAttributes(context, attrs);
        initializeViews();
        parseImageAttributes(context, attrs);
    }

    public Flip3DView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Flip3DView(final Context context) {
        super(context);
        initializeViews();
    }

    private void parsePaddingAttributes(final Context context,
            final AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Flip3DView);
        try {
            internalPadding = a.getDimensionPixelSize(
                    R.styleable.Flip3DView_internal_padding, 0);
        } finally {
            a.recycle();
        }
    }

    private void parseImageAttributes(final Context context,
            final AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Flip3DView);
        try {
            final Drawable front = a
                    .getDrawable(R.styleable.Flip3DView_src_front);
            if (front != null) {
                setImageFrontDrawable(front);
            }
            final Drawable back = a
                    .getDrawable(R.styleable.Flip3DView_src_back);
            if (back != null) {
                setImageBackDrawable(back);
            }
        } finally {
            a.recycle();
        }
    }

    private void setImageParameters(final ImageView imageView,
            final Drawable drawable) {
        if (drawable instanceof ColorDrawable) {
            imageView.setLayoutParams(colorLayoutParams);
        } else {
            imageView.setLayoutParams(layoutParams);
        }
        imageView.setImageDrawable(drawable);
    }

    /**
     * Sets front view on the control.
     * 
     * @param viewFront
     *            front view
     */
    public final void setViewFront(final View viewFront) {
        if (this.viewFront != null) {
            this.removeView(this.viewFront);
        }
        this.viewFront = viewFront;
        this.addView(viewFront);
    }

    /**
     * Sets back view on the control.
     * 
     * @param viewBack
     *            back view
     */
    public final void setViewBack(final View viewBack) {
        if (this.viewBack != null) {
            this.removeView(this.viewBack);
        }
        this.viewBack = viewBack;
        this.addView(viewBack);
    }

    /**
     * Sets image drawable for front.
     * 
     * @param drawable
     *            drawable for front.
     */
    public final void setImageFrontDrawable(final Drawable drawable) {
        final LinearLayout v = (LinearLayout) inflate(getContext(),
                R.layout.image_layout_with_padding, null);
        v.setPadding(internalPadding, internalPadding, internalPadding,
                internalPadding);
        final ImageView imageView = (ImageView) v
                .findViewById(R.id.padded_view);
        setImageParameters(imageView, drawable);
        setViewFront(v);
    }

    /**
     * Sets image drawable for back.
     * 
     * @param drawable
     *            drawable for back.
     */
    public final void setImageBackDrawable(final Drawable drawable) {
        final LinearLayout v = (LinearLayout) inflate(getContext(),
                R.layout.image_layout_with_padding, null);
        v.setPadding(internalPadding, internalPadding, internalPadding,
                internalPadding);
        final ImageView imageView = (ImageView) v
                .findViewById(R.id.padded_view);
        setImageParameters(imageView, drawable);
        setViewBack(v);
    }

    private void initializeViews() {
        setImageFrontDrawable(new ColorDrawable(Color.BLUE));
        setImageBackDrawable(new ColorDrawable(Color.RED));
    }

    private void initializeOnClickListener() {
        viewBack.setVisibility(View.INVISIBLE);
        viewFront.setVisibility(View.VISIBLE);
        viewFront.setOnClickListener(new OnClickListener());
        viewBack.setOnClickListener(new OnClickListener());
        viewFront.setClickable(true);
        viewBack.setClickable(false);
    }

    private void applyRotation(final float start, final float end) {
        // Find the center of image
        final float centerX = viewFront.getWidth() / 2.0f;
        final float centerY = viewFront.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Flip3DAnimation rotation = new Flip3DAnimation(start, end,
                centerX, centerY);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(isFirstView,
                viewFront, viewBack));
        if (isFirstView) {
            viewFront.startAnimation(rotation);
        } else {
            viewBack.startAnimation(rotation);
        }
    }

    /**
     * Starts handling of the animation.
     */
    private void startAnimationHandling() {
        if (isFirstView) {
            viewBack.setVisibility(View.INVISIBLE);
            viewFront.setVisibility(View.VISIBLE);
            viewFront.setClickable(true);
        } else {
            viewFront.setVisibility(View.INVISIBLE);
            viewBack.setVisibility(View.VISIBLE);
            viewBack.setClickable(true);
        }
        initializeOnClickListener();
    }

    @Override
    protected void onAttachedToWindow() {
        startAnimationHandling();
    }

}
