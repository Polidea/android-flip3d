package pl.polidea.androidflip3d;

/**
 * Class for controling rotation direction in non-enum mode.
 * 
 */
public final class RotationDirection {

    private RotationDirection() {
        // no instantiation.
    }

    /**
     * Rotate left.
     */
    public static final int ROTATE_LEFT = 0;
    /**
     * Rotate right.
     */
    public static final int ROTATE_RIGHT = 1;

    /**
     * Gets multiplier for rotate to left/right direction.
     * 
     * @param direction
     *            direction in which we move (0 - LEFT, 1 - RIGHT)
     * @return multiplier to apply depending on rotation (-1/1)
     */
    public static int getMultiplier(final int direction) {
        return direction == ROTATE_LEFT ? -1 : 1;
    }

    /**
     * Gets reverse of the left/right direction.
     * 
     * @param direction
     *            direction in which we move (0 - LEFT, 1 - RIGHT)
     * @return reversed rotation
     */
    public static int revertRotationDirection(final int direction) {
        return direction == ROTATE_LEFT ? ROTATE_RIGHT : ROTATE_LEFT;
    }
}
