package z.xtreamiptv.player;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapters.ReplayAdapter;
import Adapters.SeriesCategoryAdapter;
import Models.Epg;
import Models.SeriesCategory;
import Network.XtreamCodesApi;

public class SeriesCategoryActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        String catName = bundle.getString("catName");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(catName + " - Series");
        }
        setContentView((int) R.layout.activity_series_category);
        List<SeriesCategory> sc = new ArrayList<SeriesCategory>();
        sc = (ArrayList<SeriesCategory>)getIntent().getSerializableExtra("passCats");
        XtreamCodesApi api = new XtreamCodesApi();
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.live_channels);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getContext()));
        Adapter mAdapter = new SeriesCategoryAdapter(this, sc);
        mRecyclerView.setAdapter(mAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }
}
