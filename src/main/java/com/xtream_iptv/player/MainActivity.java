package com.xtream_iptv.player;

import Adapters.LiveCategoriesAdapter;
import Adapters.ReplayChannelsAdapter;
import Adapters.VodCategoriesAdapter;
import Models.Category;
import Models.Channel;
import Network.XtreamCodesApi;
import Settings.Account;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public void onResume() {
            super.onResume();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            XtreamCodesApi api = new XtreamCodesApi();
            View rootView;
            RecyclerView mRecyclerView;
            Adapter mAdapter;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    String username = "";
                    String status = "";
                    String exp_date = "";
                    rootView = inflater.inflate(R.layout.account_details_card, container, false);
                    TextView user_text = (TextView) rootView.findViewById(R.id.username_text);
                    TextView status_text = (TextView) rootView.findViewById(R.id.status_text);
                    TextView exp_text = (TextView) rootView.findViewById(R.id.expire_text);
                    try {
                        JSONObject user_auth = new JSONObject(new Account().getAuthResponse()).getJSONObject("user_info");
                        username = user_auth.getString("username");
                        status = user_auth.getString(NotificationCompat.CATEGORY_STATUS);
                        exp_date = user_auth.getString("exp_date");
                    } catch (JSONException e) {
                        Log.e("user_auth", "Json parsing error: " + e.getMessage());
                    }
                    user_text.setText(username);
                    status_text.setText(status);
                    if (((!exp_date.equalsIgnoreCase("null") ? 1 : 0) & (!exp_date.equalsIgnoreCase("") ? 1 : 0)) != 0) {
                        exp_text.setText(getDate(Long.parseLong(exp_date) * 1000));
                        return rootView;
                    }
                    exp_text.setText(R.string.no_exp_date);
                    return rootView;
                case 2:
                    List<Category> categories = Collections.emptyList();
                    rootView = inflater.inflate(R.layout.live_categories, container, false);
                    mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(false);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mAdapter = new LiveCategoriesAdapter(getActivity(), categories);
                    api.getLiveCategories((LiveCategoriesAdapter) mAdapter);
                    mRecyclerView.setAdapter(mAdapter);
                    return rootView;
                case 3:
                    List<Category> vodcategories = Collections.emptyList();
                    rootView = inflater.inflate(R.layout.live_categories, container, false);
                    mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(false);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mAdapter = new VodCategoriesAdapter(getActivity(), vodcategories);
                    api.getVodCategories((VodCategoriesAdapter) mAdapter);
                    mRecyclerView.setAdapter(mAdapter);
                    return rootView;
                case 4:
                    List<Channel> replayChan = Collections.emptyList();
                    rootView = inflater.inflate(R.layout.live_categories, container, false);
                    mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(false);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mAdapter = new ReplayChannelsAdapter(getActivity(), replayChan);
                    api.getReplayChannels((ReplayChannelsAdapter) mAdapter);
                    mRecyclerView.setAdapter(mAdapter);
                    return rootView;
                default:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                    return rootView;
            }
        }

        private String getDate(long timeStamp) {
            try {
                return new SimpleDateFormat("MM/dd/yyyy").format(new Date(timeStamp));
            } catch (Exception e) {
                return "xx";
            }
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        public int getCount() {
            return 4;
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Account";
                case 1:
                    return "Live";
                case 2:
                    return "VOD";
                case 3:
                    return "Replay";
                default:
                    return null;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        if (!(ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0 || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE"))) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
        }
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        this.mViewPager = (ViewPager) findViewById(R.id.container);
        this.mViewPager.setAdapter(this.mSectionsPagerAdapter);
        ((TabLayout) findViewById(R.id.tabs)).setupWithViewPager(this.mViewPager);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void onResume() {
        super.onResume();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {
            new Account().logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
