package z.xtreamiptv.player;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Locale;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public class TracksFragment extends Fragment {
    private TrackAdapter mAdapter;
    private ListView mTrackListView;

    public interface ITrackHolder {
        void deselectTrack(int i);

        int getSelectedTrack(int i);

        ITrackInfo[] getTrackInfo();

        void selectTrack(int i);
    }

    final class TrackAdapter extends ArrayAdapter<TrackItem> {
        private ITrackHolder mTrackHolder;
        private ITrackInfo[] mTrackInfos;

        final class ViewHolder {
            public TextView mNameTextView;

            ViewHolder() {
            }
        }

        public TrackAdapter(Context context) {
            super(context, 17367045);
        }

        public void setTrackHolder(ITrackHolder trackHolder) {
            clear();
            this.mTrackHolder = trackHolder;
            this.mTrackInfos = this.mTrackHolder.getTrackInfo();
            if (this.mTrackInfos != null) {
                for (ITrackInfo trackInfo : this.mTrackInfos) {
                    add(new TrackItem(getCount(), trackInfo));
                }
            }
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(17367045, parent, false);
            }
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.mNameTextView = (TextView) view.findViewById(16908308);
            }
            viewHolder.mNameTextView.setText(((TrackItem) getItem(position)).getInfoInline());
            return view;
        }
    }

    final class TrackItem {
        public int mIndex;
        public String mInfoInline = String.format(Locale.US, "# %d: %s", new Object[]{Integer.valueOf(this.mIndex), this.mTrackInfo.getInfoInline()});
        public ITrackInfo mTrackInfo;

        public TrackItem(int index, ITrackInfo trackInfo) {
            this.mIndex = index;
            this.mTrackInfo = trackInfo;
        }

        public String getInfoInline() {
            return this.mInfoInline;
        }
    }

    public static TracksFragment newInstance() {
        return new TracksFragment();
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_track_list, container, false);
        this.mTrackListView = (ListView) viewGroup.findViewById(R.id.track_list_view);
        return viewGroup;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        this.mAdapter = new TrackAdapter(activity);
        this.mTrackListView.setAdapter(this.mAdapter);
        if (activity instanceof ITrackHolder) {
            final ITrackHolder trackHolder = (ITrackHolder) activity;
            this.mAdapter.setTrackHolder(trackHolder);
            int selectedVideoTrack = trackHolder.getSelectedTrack(1);
            int selectedAudioTrack = trackHolder.getSelectedTrack(2);
            int selectedSubtitleTrack = trackHolder.getSelectedTrack(3);
            if (selectedVideoTrack >= 0) {
                this.mTrackListView.setItemChecked(selectedVideoTrack, true);
            }
            if (selectedAudioTrack >= 0) {
                this.mTrackListView.setItemChecked(selectedAudioTrack, true);
            }
            if (selectedSubtitleTrack >= 0) {
                this.mTrackListView.setItemChecked(selectedSubtitleTrack, true);
            }
            this.mTrackListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    TrackItem trackItem = (TrackItem) TracksFragment.this.mTrackListView.getItemAtPosition(position);
                    int i = 0;
                    while (i < TracksFragment.this.mAdapter.getCount()) {
                        TrackItem compareItem = (TrackItem) TracksFragment.this.mAdapter.getItem(i);
                        if (compareItem.mIndex != trackItem.mIndex && compareItem.mTrackInfo.getTrackType() == trackItem.mTrackInfo.getTrackType() && TracksFragment.this.mTrackListView.isItemChecked(i)) {
                            TracksFragment.this.mTrackListView.setItemChecked(i, false);
                        }
                        i++;
                    }
                    if (TracksFragment.this.mTrackListView.isItemChecked(position)) {
                        trackHolder.selectTrack(trackItem.mIndex);
                    } else {
                        trackHolder.deselectTrack(trackItem.mIndex);
                    }
                }
            });
            return;
        }
        Log.e("TracksFragment", "activity is not an instance of ITrackHolder.");
    }
}
