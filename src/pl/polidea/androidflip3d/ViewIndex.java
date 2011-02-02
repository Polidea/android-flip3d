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

    /**
     * Front transparent background view. Used to block clicks while flipping.
     */
    public static final int FOREGROUND_VIEW = 3;

    /**
     * Number of views..
     */
    public static final int VIEW_NUMBER = 4;

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

    /**
     * Return textual description of view type
     * 
     * @param id
     *            id of the view
     * @return type
     */
    public static final String getViewType(final int id) {
        switch (id) {
        case FRONT_VIEW:
            return "FRONT";
        case BACK_VIEW:
            return "BACK";
        case FOREGROUND_VIEW:
            return "FOREGROUND";
        default:
            return "BACKGROUND";
        }
    }
}
