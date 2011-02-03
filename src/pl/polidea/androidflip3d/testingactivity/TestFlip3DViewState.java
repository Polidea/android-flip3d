package pl.polidea.androidflip3d.testingactivity;

import pl.polidea.androidflip3d.Flip3DViewState;

public class TestFlip3DViewState extends Flip3DViewState {

    private final int color;

    public TestFlip3DViewState(final int id, final int color) {
        super(id);
        this.color = color;
    }

    public int getColor() {
        return color;
    }

}
