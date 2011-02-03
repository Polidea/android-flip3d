package pl.polidea.androidflip3d.testingactivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

/**
 * Shows grid of Flip3D.
 * 
 */
public class GridTestActivity extends Activity {
    private static final String TAG = GridTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getResources().getIdentifier("test_activity_grid",
                "layout", "pl.polidea.androidflip3d"));
        final GridView gridView = (GridView) findViewById(this.getResources()
                .getIdentifier("GridView", "id", "pl.polidea.androidflip3d"));
        final int screenWidth = this.getWindow().getWindowManager()
                .getDefaultDisplay().getWidth();
        Log.v(TAG, "Screen width = " + screenWidth);
        final TestGridAdapter adapter = new TestGridAdapter(this,
                screenWidth / 3);
        gridView.setAdapter(adapter);
    }
}
