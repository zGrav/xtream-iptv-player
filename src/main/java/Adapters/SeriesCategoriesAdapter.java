package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Models.Category;
import Models.SeriesCategory;
import Network.XtreamCodesApi;
import z.xtreamiptv.player.R;
import z.xtreamiptv.player.ReplayActivity;
import z.xtreamiptv.player.SeriesCategoryActivity;

public class SeriesCategoriesAdapter extends RecyclerView.Adapter<SeriesCategoriesAdapter.ViewHolder> {
    private List<Category> categories;
    private List<SeriesCategory> seriesCategory;
    private Context mContext;

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        CardView category_card;
        TextView category_name;

        ViewHolder(View v) {
            super(v);
            this.category_name = (TextView) v.findViewById(R.id.category_name);
            this.category_card = (CardView) v.findViewById(R.id.category_card);
            v.setOnClickListener(this);
        }

        public void onClick(View v) {
            XtreamCodesApi api = new XtreamCodesApi();
            api.getSpecificSeriesCategory(SeriesCategoriesAdapter.this, SeriesCategoriesAdapter.this.categories.get(getAdapterPosition()).getCategory_id(), SeriesCategoriesAdapter.this.categories.get(getAdapterPosition()).getCategory_name());
        }
    }

    public void setSeriesCategories(List<Category> cats) {
        if (cats.size() == 0) {
            ((Activity) mContext).finish();
            Toast.makeText(mContext, mContext.getResources().getString(R.string.no_categories),
                    Toast.LENGTH_LONG).show();
        }
        this.categories = cats;
        notifyItemRangeChanged(0, this.categories.size());
    }

    public void setSpecificSeriesCategory(List<SeriesCategory> cats, String catName) {
        if (cats.size() == 0) {
            ((Activity) mContext).finish();
            Toast.makeText(mContext, mContext.getResources().getString(R.string.no_categories),
                    Toast.LENGTH_LONG).show();
        }
        this.seriesCategory = cats;
        notifyItemRangeChanged(0, this.seriesCategory.size());

        SeriesCategoriesAdapter.this.mContext.startActivity(new Intent(SeriesCategoriesAdapter.this.mContext, SeriesCategoryActivity.class).putExtra("passCats", (ArrayList<SeriesCategory>) cats).putExtra("catName", catName));
    }

    public SeriesCategoriesAdapter(Context context, List<Category> cats) {
        this.mContext = context;
        this.categories = cats;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.category_layout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.category_name.setText(((Category) this.categories.get(position)).getCategory_name());
    }

    public int getItemCount() {
        if (this.categories == null) {
            return 0;
        }
        return this.categories.size();
    }
}
