package Adapters;

import Models.Movie;
import Network.VolleySingleton;
import Settings.Account;
import Settings.Setting;

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
import z.xtreamiptv.player.R;
import z.xtreamiptv.player.VideoActivity;
import java.util.ArrayList;
import java.util.List;

public class VodStreamsAdapter extends RecyclerView.Adapter<VodStreamsAdapter.ViewHolder> {
    private List<Movie> allMovies = new ArrayList();
    private Context mContext;
    private List<Movie> movies;

    class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements OnClickListener {
        ImageView default_icon;
        NetworkImageView imageView;
        TextView movie_name;

        ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (NetworkImageView) itemView.findViewById(R.id.movie_logo);
            this.movie_name = (TextView) itemView.findViewById(R.id.movie_name);
            this.default_icon = (ImageView) itemView.findViewById(R.id.defaul_icon);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            Account account = new Account();
            Setting setting = new Setting(VodStreamsAdapter.this.mContext);
            Uri target = Uri.parse(account.getHost() + "movie/" + account.getUsername() + "/" + account.getPassword() + "/" + ((Movie) VodStreamsAdapter.this.movies.get(getAdapterPosition())).getStream_id() + "." + ((Movie) VodStreamsAdapter.this.movies.get(getAdapterPosition())).getContainer_extension());
            if (setting.getPlayer() == 3) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setDataAndType(target, "video");
                VodStreamsAdapter.this.mContext.startActivity(Intent.createChooser(intent, "Please choose a player"));
                return;
            }
            VideoActivity.intentTo(VodStreamsAdapter.this.mContext, target.toString(), ((Movie) VodStreamsAdapter.this.movies.get(getAdapterPosition())).getName());
        }
    }

    public VodStreamsAdapter(List<Movie> movies, Context mContext) {
        this.movies = movies;
        this.mContext = mContext;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.movie_layout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (((Movie) this.movies.get(position)).getStream_icon().length() > 0) {
            holder.imageView.setVisibility(0);
            holder.default_icon.setVisibility(8);
            holder.imageView.setImageUrl(((Movie) this.movies.get(position)).getStream_icon(), VolleySingleton.getInstance().getImageLoader());
        } else {
            holder.imageView.setVisibility(8);
            holder.default_icon.setVisibility(0);
        }
        holder.movie_name.setText(((Movie) this.movies.get(position)).getName());
    }

    public int getItemCount() {
        if (this.movies == null) {
            return 0;
        }
        return this.movies.size();
    }

    public void setMovies(List<Movie> movies) {
        if (movies.size() == 0) {
            ((Activity) mContext).finish();
            Toast.makeText(mContext, mContext.getResources().getString(R.string.no_movies),
                    Toast.LENGTH_LONG).show();
        }
        this.allMovies.addAll(movies);
        this.movies = movies;
        notifyItemRangeChanged(0, movies.size());
    }

    public void setFilter(String query) {
        this.movies = new ArrayList();
        for (Movie movie : this.allMovies) {
            if (movie.getName().toLowerCase().contains(query.toLowerCase())) {
                this.movies.add(movie);
            }
        }
        notifyDataSetChanged();
    }
}
