package Adapters;

import Models.Channel;
import Network.VolleySingleton;
import Network.XtreamCodesApi;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.xtream_iptv.player.R;
import com.xtream_iptv.player.ReplayActivity;
import java.util.List;

public class ReplayChannelsAdapter extends RecyclerView.Adapter<ReplayChannelsAdapter.ViewHolder> {
    private XtreamCodesApi api = new XtreamCodesApi();
    private List<Channel> channels;
    private Context mContext;
    private ReplayChannelsAdapter replayChannelsAdapter = this;

    class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements OnClickListener {
        TextView channel_name;
        NetworkImageView net_logo;
        TextView prog_end;
        TextView prog_start;
        TextView prog_title;

        ViewHolder(View itemView) {
            super(itemView);
            this.net_logo = (NetworkImageView) itemView.findViewById(R.id.channel_logo);
            this.channel_name = (TextView) itemView.findViewById(R.id.channel_name);
            this.prog_title = (TextView) itemView.findViewById(R.id.epg_title);
            this.prog_start = (TextView) itemView.findViewById(R.id.epg_start);
            this.prog_end = (TextView) itemView.findViewById(R.id.epg_end);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            ReplayChannelsAdapter.this.mContext.startActivity(new Intent(ReplayChannelsAdapter.this.mContext, ReplayActivity.class).putExtra("channel_name", ((Channel) ReplayChannelsAdapter.this.channels.get(getAdapterPosition())).getName()).putExtra("channel_id", ((Channel) ReplayChannelsAdapter.this.channels.get(getAdapterPosition())).getStream_id()));
        }
    }

    public ReplayChannelsAdapter(Context context, List<Channel> channels) {
        this.channels = channels;
        this.mContext = context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.channel_layout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageLoader imageLoader = VolleySingleton.getInstance().getImageLoader();
        if (((Channel) this.channels.get(position)).getStream_icon().length() > 0) {
            holder.net_logo.setImageUrl(((Channel) this.channels.get(position)).getStream_icon(), imageLoader);
        } else {
            holder.net_logo.setImageUrl("http://streaming-hub.com/xtream-iptv-player-logo.png", imageLoader);
        }
        holder.channel_name.setText(((Channel) this.channels.get(position)).getName());
    }

    public int getItemCount() {
        if (this.channels == null) {
            return 0;
        }
        return this.channels.size();
    }

    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        VolleySingleton.getInstance().getRequestQueue().cancelAll(Boolean.valueOf(true));
    }

    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        VolleySingleton.getInstance().getRequestQueue().cancelAll(Boolean.valueOf(true));
    }

    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        VolleySingleton.getInstance().getRequestQueue().cancelAll(Boolean.valueOf(true));
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
        notifyItemRangeChanged(0, channels.size());
    }
}
