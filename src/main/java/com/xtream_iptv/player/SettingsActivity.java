package com.xtream_iptv.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity.Header;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new C06891();

    static class C06891 implements OnPreferenceChangeListener {
        C06891() {
        }

        public boolean onPreferenceChange(Preference preference, Object value) {
            CharSequence charSequence = null;
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                if (index >= 0) {
                    charSequence = listPreference.getEntries()[index];
                }
                preference.setSummary(charSequence);
            } else if (!(preference instanceof RingtonePreference)) {
                preference.setSummary(stringValue);
            } else if (TextUtils.isEmpty(stringValue)) {
                preference.setSummary(R.string.pref_ringtone_silent);
            } else {
                Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));
                if (ringtone == null) {
                    preference.setSummary(null);
                } else {
                    preference.setSummary(ringtone.getTitle(preference.getContext()));
                }
            }
            return true;
        }
    }

    @TargetApi(11)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() != 16908332) {
                return super.onOptionsItemSelected(item);
            }
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
    }

    @TargetApi(11)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("pref.player"));
        }

        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() != 16908332) {
                return super.onOptionsItemSelected(item);
            }
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
    }

    @TargetApi(11)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() != 16908332) {
                return super.onOptionsItemSelected(item);
            }
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 4;
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onMenuItemSelected(featureId, item);
        }
        if (!super.onMenuItemSelected(featureId, item)) {
            NavUtils.navigateUpFromSameTask(this);
        }
        finish();
        return true;
    }

    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @TargetApi(11)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName) || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }
}
