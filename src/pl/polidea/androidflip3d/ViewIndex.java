package pl.polidea.androidflip3d;

/**
 * Non-enum implementation of front/back view index.
 * 
 */
public final class ViewIndex {
    /**
     * Front view.
     */
    public static final int FRONT_VIEW = 0;
    /**
     * Back view.
     */
    public static final int BACK_VIEW = 1;

    private ViewIndex() {
        // no instantiation.
    }

    /**
     * Get the other view index.
     * 
     * @param viewIndex
     *            index
     * @return the other index
     */
    public static int getTheOtherViewIndex(final int viewIndex) {
        return viewIndex == FRONT_VIEW ? BACK_VIEW : FRONT_VIEW;
    }
}
