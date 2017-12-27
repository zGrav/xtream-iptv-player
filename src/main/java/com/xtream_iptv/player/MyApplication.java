package com.xtream_iptv.player;

import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class MyApplication extends Application {
    private static MyApplication sInstance;

    public static class AccountFragment extends Fragment {
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            String username = "";
            String password = "";
            String status = "";
            String exp_date = "";
            View rootView = inflater.inflate(R.layout.account_details_card, container, false);
            TextView user_text = (TextView) rootView.findViewById(R.id.username_text);
            TextView status_text = (TextView) rootView.findViewById(R.id.status_text);
            TextView exp_text = (TextView) rootView.findViewById(R.id.expire_text);
            try {
                JSONObject user_auth = new JSONObject(getActivity().getIntent().getStringExtra("auth")).getJSONObject("user_info");
                username = user_auth.getString("username");
                password = user_auth.getString("password");
                status = user_auth.getString(NotificationCompat.CATEGORY_STATUS);
                exp_date = user_auth.getString("exp_date");
            } catch (JSONException e) {
                Log.e("user_auth", "Json parsing error: " + e.getMessage());
            }
            user_text.setText(username);
            status_text.setText(status);
            exp_text.setText(exp_date);
            return rootView;
        }
    }

    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getContext() {
        return sInstance.getApplicationContext();
    }
}
