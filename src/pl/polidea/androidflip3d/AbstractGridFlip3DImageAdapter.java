package pl.polidea.androidflip3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.RecyclerListener;
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
public abstract class AbstractGridFlip3DImageAdapter<State extends Flip3DViewState, FlipView extends Flip3DView>
        extends BaseAdapter implements RecyclerListener {
    static final int MAX_IMAGES = 20;

    private final List<State> viewStates = new ArrayList<State>(MAX_IMAGES);

    private final int itemWidth;

    public AbstractGridFlip3DImageAdapter(final int itemWidth) {
        super();
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
    public Object getItem(final int position) { // NOPMD
        return null;
    }

    @Override
    public long getItemId(final int position) { // NOPMD
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
        if (convertView == null) {
            newView = createView();
        } else {
            newView = (FlipView) convertView;
        }
        prepareView(position, newView);
        newView.setLayoutParams(new GridView.LayoutParams(itemWidth, itemWidth));
        return newView;
    }

    public synchronized List<State> getViewStates() {
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

    @Override
    public synchronized void onMovedToScrapHeap(final View view) {
        for (final Flip3DViewState s : viewStates) {
            s.detachView((Flip3DView) view);
        }
    }

}