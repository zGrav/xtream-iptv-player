package Adapters;

import Models.Channel;
import Models.Epg;
import Network.VolleySingleton;
import Network.XtreamCodesApi;
import Settings.Account;
import Settings.Setting;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.xtream_iptv.player.R;
import com.xtream_iptv.player.VideoActivity;
import java.util.ArrayList;
import java.util.List;

public class LiveChannelsAdapter extends RecyclerView.Adapter<LiveChannelsAdapter.ViewHolder> {
    private List<Channel> allChannels = new ArrayList();
    private XtreamCodesApi api = new XtreamCodesApi();
    private LiveChannelsAdapter channelAdapter = this;
    private List<Channel> channels;
    private Context mContext;

    class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements OnClickListener {
        TextView channel_name;
        ImageView default_icon;
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
            this.default_icon = (ImageView) itemView.findViewById(R.id.defaul_icon);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            Setting setting = new Setting(LiveChannelsAdapter.this.mContext);
            Account account = new Account();
            Uri target = Uri.parse(account.getHost() + "live/" + account.getUsername() + "/" + account.getPassword() + "/" + ((Channel) LiveChannelsAdapter.this.channels.get(getAdapterPosition())).getStream_id() + ".ts");
            if (setting.getPlayer() == 3) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setDataAndType(target, "video/*");
                LiveChannelsAdapter.this.mContext.startActivity(Intent.createChooser(intent, "Please choose a player"));
                return;
            }
            VideoActivity.intentTo(LiveChannelsAdapter.this.mContext, target.toString(), ((Channel) LiveChannelsAdapter.this.channels.get(getAdapterPosition())).getName());
        }
    }

    public LiveChannelsAdapter(Context context, List<Channel> channels) {
        this.channels = channels;
        this.mContext = context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.channel_layout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (((Channel) this.channels.get(position)).getStream_icon().length() > 0) {
            holder.net_logo.setVisibility(0);
            holder.default_icon.setVisibility(8);
            holder.net_logo.setImageUrl(((Channel) this.channels.get(position)).getStream_icon(), VolleySingleton.getInstance().getImageLoader());
        } else {
            holder.net_logo.setVisibility(8);
            holder.default_icon.setVisibility(0);
        }
        holder.channel_name.setText(((Channel) this.channels.get(position)).getName());
        if (((Channel) this.channels.get(position)).getEpg_channel_id() == "null") {
            holder.prog_title.setText(R.string.no_epg);
        } else if (((Channel) this.channels.get(position)).getEpgData().getTitle() == "") {
            this.api.getEpg(((Channel) this.channels.get(position)).getStream_id(), position, this.channelAdapter);
            Epg prog = ((Channel) this.channels.get(position)).getEpgData();
            if (prog == null) {
                holder.prog_title.setText(R.string.no_epg);
                return;
            }
            holder.prog_title.setText(prog.getTitle());
            holder.prog_start.setText(prog.getStart());
            holder.prog_end.setText(prog.getEnd());
        } else {
            holder.prog_title.setText(((Channel) this.channels.get(position)).getEpgData().getTitle());
            holder.prog_start.setText(((Channel) this.channels.get(position)).getEpgData().getStart());
            holder.prog_end.setText(((Channel) this.channels.get(position)).getEpgData().getEnd());
        }
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

    public void setChannelEpg(Epg channelEpg, int position) {
        try {
            ((Channel) this.channels.get(position)).setEpg_data(channelEpg);
            notifyItemChanged(position);
        } catch (IndexOutOfBoundsException e) {
            Log.w("SetChannelEPG: ", e.getMessage());
        }
    }

    public void setFilter(String query) {
        this.channels = new ArrayList();
        for (Channel channel : this.allChannels) {
            if (channel.getName().toLowerCase().contains(query.toLowerCase())) {
                this.channels.add(channel);
            }
        }
        notifyDataSetChanged();
    }

    public void setLiveChannels(List<Channel> channels) {
        this.allChannels.addAll(channels);
        this.channels = channels;
        notifyItemRangeChanged(0, channels.size());
    }
}
