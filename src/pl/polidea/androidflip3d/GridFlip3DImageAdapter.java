package pl.polidea.androidflip3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

/**
 * Adapter for Flip Images displayed in Grid. Works around a nasty (really
 * nasty!) Android problem with many calls to the same item in a grid.
 * 
 * @param <State>
 *            state where information about view state are stored
 * @param <FlipView>
 *            the view itself
 * 
 */
public abstract class GridFlip3DImageAdapter<State extends Flip3DViewState, FlipView extends Flip3DView> extends
        BaseAdapter {
    private static final String TAG = GridFlip3DImageAdapter.class.getSimpleName();
    static final int MAX_IMAGES = 20;

    private final List<State> viewStates = new ArrayList<State>(MAX_IMAGES);

    private final int itemWidth;

    public GridFlip3DImageAdapter(final int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public synchronized void setViewStates(final List<State> statesToSet) {
        viewStates.clear();
        for (final State state : statesToSet) {
            viewStates.add(state);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return viewStates.size();
    }

    @Override
    public Object getItem(final int position) {
        return null;
    }

    @Override
    public long getItemId(final int position) {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized View getView(final int position, final View convertView, final ViewGroup parent) {
        if (checkifExtraCall(position, convertView)) { // DIRTY HACK ... BUT
                                                       // THIS IS THE ONLY WAY
                                                       // WITH GRID VIEW
            return convertView == null ? createView() : convertView;
        }
        FlipView newView;
        Log.v(TAG, "getView at position " + position + ". Previous view : " + convertView + " Parent" + parent);
        if (convertView != null) {
            Log.v(TAG, "Reusing view at position " + position);
            newView = (FlipView) convertView;
        } else {
            Log.v(TAG, "Creating new view at position " + position);
            newView = createView();
        }
        prepareView(position, newView);
        newView.setLayoutParams(new GridView.LayoutParams(itemWidth, itemWidth));
        return newView;
    }

    public List<State> getViewStates() {
        return Collections.unmodifiableList(viewStates);
    }

    protected abstract FlipView createView();

    protected abstract void prepareView(int position, FlipView view);

    private boolean checkifExtraCall(final int position, final View convertView) {
        // XXX: HACK. Grid view uses multiple calls to first item in order
        // to calculate size of the grid!!!
        // Which is really a hack as I see (but not some android
        // developers!):
        // http://www.mail-archive.com/android-developers@googlegroups.com/msg57716.html
        if (position == 0 && convertView != null && convertView.getId() == 0) {
            return true;
        }
        State viewState = null;
        if (position >= 0 && position < viewStates.size()) {
            viewState = viewStates.get(position);
            if (viewState != null && viewState.getView() != null) {
                return position == 0;
            }
        }
        return false;
    }
}