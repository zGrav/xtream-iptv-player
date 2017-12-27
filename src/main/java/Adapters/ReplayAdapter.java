package Adapters;

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
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.xtream_iptv.player.R;
import com.xtream_iptv.player.VideoActivity;
import java.util.List;

public class ReplayAdapter extends RecyclerView.Adapter<ReplayAdapter.ViewHolder> {
    private XtreamCodesApi api = new XtreamCodesApi();
    private String channel_id;
    private Context mContext;
    private List<Epg> progs;
    private ReplayAdapter replayAdapter = this;

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
            Account account = new Account();
            Setting setting = new Setting(ReplayAdapter.this.mContext);
            String start = ((Epg) ReplayAdapter.this.progs.get(getAdapterPosition())).getReplay_time();
            String duration = String.valueOf(((Epg) ReplayAdapter.this.progs.get(getAdapterPosition())).get_duration());
            Log.i("shadow", start + " | " + duration);
            Uri target = Uri.parse(account.getHost() + "timeshift/" + account.getUsername() + "/" + account.getPassword() + "/" + duration + "/" + start + "/" + ReplayAdapter.this.channel_id + ".m3u8");
            if (setting.getPlayer() == 3) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setDataAndType(target, "video/*");
                ReplayAdapter.this.mContext.startActivity(Intent.createChooser(intent, "Please choose a player"));
                return;
            }
            VideoActivity.intentTo(ReplayAdapter.this.mContext, target.toString(), ((Epg) ReplayAdapter.this.progs.get(getAdapterPosition())).getTitle());
        }
    }

    public ReplayAdapter(Context context, List<Epg> progs) {
        this.progs = progs;
        this.mContext = context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.channel_layout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.channel_name.setText(((Epg) this.progs.get(position)).getTitle());
        holder.prog_title.setText(((Epg) this.progs.get(position)).getTitle());
        holder.prog_start.setText(((Epg) this.progs.get(position)).get_date());
        holder.prog_end.setText(((Epg) this.progs.get(position)).getStart() + " - " + ((Epg) this.progs.get(position)).getEnd());
    }

    public int getItemCount() {
        if (this.progs == null) {
            return 0;
        }
        return this.progs.size();
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

    public void setProgs(List<Epg> progs, int id) {
        this.progs = progs;
        this.channel_id = String.valueOf(id);
        notifyItemRangeChanged(0, progs.size());
    }
}
