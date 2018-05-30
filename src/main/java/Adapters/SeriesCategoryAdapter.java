package Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Models.Channel;

import Models.SeriesCategory;
import Network.VolleySingleton;
import Network.XtreamCodesApi;
import z.xtreamiptv.player.R;
import z.xtreamiptv.player.ReplayActivity;
import z.xtreamiptv.player.SeriesActivity;

public class SeriesCategoryAdapter extends Adapter<SeriesCategoryAdapter.ViewHolder> {
    private List<SeriesCategory> sc;
    private JSONObject series;
    private Context mContext;

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        TextView sc_name;
        NetworkImageView sc_logo;

        ViewHolder(View itemView) {
            super(itemView);
            this.sc_logo = (NetworkImageView) itemView.findViewById(R.id.sc_logo);
            this.sc_name = (TextView) itemView.findViewById(R.id.sc_name);

            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            XtreamCodesApi api = new XtreamCodesApi();
            api.getSpecificSeries(SeriesCategoryAdapter.this, SeriesCategoryAdapter.this.sc.get(getAdapterPosition()).getSeries_id(), SeriesCategoryAdapter.this.sc.get(getAdapterPosition()).getName());
        }
    }

    public SeriesCategoryAdapter(Context context, List<SeriesCategory> sc) {
        this.sc = sc;
        this.mContext = context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.series_category_layout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageLoader imageLoader = VolleySingleton.getInstance().getImageLoader();
        if (((SeriesCategory) this.sc.get(position)).getCover().length() > 0) {
            holder.sc_logo.setImageUrl(((SeriesCategory) this.sc.get(position)).getCover(), imageLoader);
        } else {
            holder.sc_logo.setImageUrl("http://streaming-hub.com/xtream-iptv-player-logo.png", imageLoader);
        }
        holder.sc_name.setText(((SeriesCategory) this.sc.get(position)).getName());
    }

    public int getItemCount() {
        if (this.sc == null) {
            return 0;
        }
        return this.sc.size();
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

    public void setSc(List<SeriesCategory> sc) {
        this.sc = sc;
        notifyItemRangeChanged(0, sc.size());
    }

    public void setSeries(JSONObject series, String seriesName) {
        this.series = series;

        SeriesCategoryAdapter.this.mContext.startActivity(new Intent(SeriesCategoryAdapter.this.mContext, SeriesActivity.class).putExtra("passSeries", series.toString()).putExtra("seriesName", seriesName));

    }
}
