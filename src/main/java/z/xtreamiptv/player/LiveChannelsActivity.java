package z.xtreamiptv.player;

import Adapters.LiveChannelsAdapter;
import Models.Channel;
import Network.XtreamCodesApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import java.util.Collections;
import java.util.List;

public class LiveChannelsActivity extends AppCompatActivity implements OnQueryTextListener {
    LiveChannelsAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        CharSequence category_name = bundle.getString("name");
        int category_id = bundle.getInt("id");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(category_name);
        }
        setContentView((int) R.layout.activity_live_channels);
        List<Channel> channels = Collections.emptyList();
        XtreamCodesApi api = new XtreamCodesApi();
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.live_channels);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getContext()));
        this.mAdapter = new LiveChannelsAdapter(this, channels);
        api.getCategoryChannels(category_id, this.mAdapter);
        mRecyclerView.setAdapter(this.mAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        ((SearchView) menu.findItem(R.id.action_search).getActionView()).setOnQueryTextListener(this);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        this.mAdapter.setFilter(newText);
        return true;
    }
}
