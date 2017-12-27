package Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.xtream_iptv.player.MyApplication;

public class Account {
    private static final String KEY_AUTH_RESPONSE = "auth_response";
    private static final String KEY_HOST = "host";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USERNAME = "username";
    private static final String PREF_NAME = "XtreamIptvPlayer";
    private static String TAG = Account.class.getSimpleName();
    private int PRIVATE_MODE = 0;
    private Context context = MyApplication.getContext();
    private SharedPreferences pref = this.context.getSharedPreferences(PREF_NAME, this.PRIVATE_MODE);

    public String getAuthResponse() {
        return this.pref.getString(KEY_AUTH_RESPONSE, "");
    }

    public void setAuthResponse(String authResponse) {
        this.pref.edit().putString(KEY_AUTH_RESPONSE, authResponse).apply();
    }

    public void save(String host, String username, String password) {
        Editor editor = this.pref.edit();
        editor.putString(KEY_HOST, host);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_IS_LOGGEDIN, true);
        editor.apply();
    }

    public String getHost() {
        return this.pref.getString(KEY_HOST, "");
    }

    public String getUsername() {
        return this.pref.getString(KEY_USERNAME, "");
    }

    public String getPassword() {
        return this.pref.getString(KEY_PASSWORD, "");
    }

    public Boolean isLoggedIn() {
        return Boolean.valueOf(this.pref.getBoolean(KEY_IS_LOGGEDIN, false));
    }

    public void logout() {
        this.pref.edit().putBoolean(KEY_IS_LOGGEDIN, false).apply();
    }
}
