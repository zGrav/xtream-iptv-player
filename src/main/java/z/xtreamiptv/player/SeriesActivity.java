package z.xtreamiptv.player;

import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import Adapters.SeriesAdapter;
import Adapters.VodStreamsAdapter;
import Models.Movie;

import Network.XtreamCodesApi;

public class SeriesActivity extends AppCompatActivity {
    SeriesAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        CharSequence series_name = bundle.getString("seriesName");
        CharSequence series = bundle.getString("passSeries");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(series_name);
        }
        setContentView((int) R.layout.activity_series);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.series);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getContext()));
        JSONObject jsonSeries = null;
        try {
            jsonSeries = new JSONObject((String) series);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList grabSeries = new ArrayList();
        Iterator<?> keys = jsonSeries.keys();
        while(keys.hasNext() ) {
            String key = (String)keys.next();
            try {
                if ( jsonSeries.get(key) instanceof JSONArray) {
                    JSONArray grab = jsonSeries.getJSONArray(key);

                    for (int i = 0; i < grab.length(); i++) {
                        JSONObject parsed = new JSONObject(grab.get(i).toString());
                        grabSeries.add(parsed);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.mAdapter = new SeriesAdapter(grabSeries, this);
        mRecyclerView.setAdapter(this.mAdapter);

        AlertDialog alertDialog = new AlertDialog.Builder(SeriesActivity.this).create();

        alertDialog.setTitle(getResources().getString(R.string.vlc_warning));
        alertDialog.setMessage(getResources().getString(R.string.vlc_warning_desc));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        if (!isPackageExists("videolan.vlc")) {
            alertDialog.show();
        }
    }

    public boolean isPackageExists(String targetPackage){
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.indexOf(targetPackage) > -1)
                return true;
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }
}
