package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Models.Movie;

import Network.VolleySingleton;
import Settings.Account;
import Settings.Setting;
import z.xtreamiptv.player.R;
import z.xtreamiptv.player.VideoActivity;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList series;

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ImageView default_icon;
        NetworkImageView imageView;
        TextView series_name;

        ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (NetworkImageView) itemView.findViewById(R.id.series_logo);
            this.series_name = (TextView) itemView.findViewById(R.id.series_name);
            this.default_icon = (ImageView) itemView.findViewById(R.id.defaul_icon);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            Account account = new Account();
            Setting setting = new Setting(SeriesAdapter.this.mContext);
            JSONObject parsed = null;
            try {
                parsed = new JSONObject(series.get(getAdapterPosition()).toString());

                Uri target = Uri.parse(account.getHost() + "series/" + account.getUsername() + "/" + account.getPassword() + "/" + parsed.getString("id") + "." + parsed.getString("container_extension"));
                if (setting.getPlayer() == 3) {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setDataAndType(target, "video");
                    SeriesAdapter.this.mContext.startActivity(Intent.createChooser(intent, "Please choose a player"));
                    return;
                }
                VideoActivity.intentTo(SeriesAdapter.this.mContext, target.toString(), parsed.getString("title"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public SeriesAdapter(ArrayList series, Context mContext) {
        this.series = series;
        this.mContext = mContext;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.series_layout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
            JSONObject parsed = null;
            try {
                parsed = new JSONObject(this.series.get(position).toString());


                if (parsed.getJSONObject("info").getString("movie_image").length() > 0) {
                    holder.imageView.setVisibility(0);
                    holder.default_icon.setVisibility(8);
                    holder.imageView.setImageUrl(parsed.getJSONObject("info").getString("movie_image"), VolleySingleton.getInstance().getImageLoader());
                } else {
                    holder.imageView.setVisibility(8);
                    holder.default_icon.setVisibility(0);
                }
                holder.series_name.setText(parsed.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public int getItemCount() {
        if (this.series == null) {
            return 0;
        }
        return this.series.size();
    }

    public void setSeries(ArrayList series) {
        if (series.size() == 0) {
            ((Activity) mContext).finish();
            Toast.makeText(mContext, mContext.getResources().getString(R.string.no_movies),
                    Toast.LENGTH_LONG).show();
        }

        this.series = series;
    }
}
