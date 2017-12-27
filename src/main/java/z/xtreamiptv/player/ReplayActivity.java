package z.xtreamiptv.player;

import Adapters.ReplayAdapter;
import Models.Epg;
import Network.XtreamCodesApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.MenuItem;
import java.util.Collections;
import java.util.List;

public class ReplayActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        String channel_name = bundle.getString("channel_name");
        int channel_id = bundle.getInt("channel_id");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(channel_name + " Replay");
        }
        setContentView((int) R.layout.activity_live_channels);
        List<Epg> progs = Collections.emptyList();
        XtreamCodesApi api = new XtreamCodesApi();
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.live_channels);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getContext()));
        Adapter mAdapter = new ReplayAdapter(this, progs);
        api.getReplayEpg(channel_id, (ReplayAdapter) mAdapter);
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
