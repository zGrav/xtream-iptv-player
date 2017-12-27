package z.xtreamiptv.player;

import Network.VolleySingleton;
import Settings.Account;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class LoginActivity extends AppCompatActivity {
    private Account account;
    private String host;
    private TextView mHost;
    private View mLoginFormView;
    private EditText mPasswordView;
    private View mProgressView;
    private TextView mUsernameView;
    private String password;
    private String username;

    class C06841 implements OnEditorActionListener {
        C06841() {
        }

        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            if (id != R.id.login && id != 0) {
                return false;
            }
            LoginActivity.this.attemptLogin();
            return true;
        }
    }

    class C06852 implements OnClickListener {
        C06852() {
        }

        public void onClick(View view) {
            LoginActivity.this.attemptLogin();
        }
    }

    class C09896 implements ErrorListener {
        C09896() {
        }

        public void onErrorResponse(VolleyError error) {
            LoginActivity.this.showProgress(false);
            Toast.makeText(MyApplication.getContext(), "Error fetching data from server", 1).show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.account = new Account();
        setContentView((int) R.layout.activity_login);
        this.mHost = (TextView) findViewById(R.id.host);
        this.mUsernameView = (TextView) findViewById(R.id.username);
        this.mPasswordView = (EditText) findViewById(R.id.password);
        this.mPasswordView.setOnEditorActionListener(new C06841());
        ((Button) findViewById(R.id.sign_in_button)).setOnClickListener(new C06852());
        this.mLoginFormView = findViewById(R.id.login_form);
        this.mProgressView = findViewById(R.id.login_progress);
        if (this.account.isLoggedIn().booleanValue()) {
            attemptLogin();
            return;
        }
        this.host = this.account.getHost();
        this.username = this.account.getUsername();
        this.password = this.account.getPassword();
        this.mHost.setText(this.host);
        this.mUsernameView.setText(this.username);
        this.mPasswordView.setText(this.password);
    }

    private void attemptLogin() {
        if (this.account.isLoggedIn().booleanValue()) {
            this.host = this.account.getHost();
            this.username = this.account.getUsername();
            this.password = this.account.getPassword();
            this.mHost.setText(this.host);
            this.mUsernameView.setText(this.username);
            this.mPasswordView.setText(this.password);
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        this.mHost.setError(null);
        this.mUsernameView.setError(null);
        this.mPasswordView.setError(null);
        this.host = this.mHost.getText().toString();
        this.username = this.mUsernameView.getText().toString();
        this.password = this.mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(this.password)) {
            this.mPasswordView.setError(getString(R.string.error_field_required));
            focusView = this.mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(this.username)) {
            this.mUsernameView.setError(getString(R.string.error_field_required));
            focusView = this.mUsernameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(this.host)) {
            this.mHost.setError(getString(R.string.error_field_required));
            focusView = this.mHost;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            return;
        }
        showProgress(true);
        UserLoginTask(this.host, this.username, this.password);
    }

    @TargetApi(13)
    private void showProgress(final boolean show) {
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        int i = 8;
        int i2 = 0;
        int i3;
        if (VERSION.SDK_INT >= 13) {
            int shortAnimTime = getResources().getInteger(17694720);
            View view = this.mLoginFormView;
            if (show) {
                i3 = 8;
            } else {
                i3 = 0;
            }
            view.setVisibility(i3);
            this.mLoginFormView.animate().setDuration((long) shortAnimTime).alpha(show ? 0.0f : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    LoginActivity.this.mLoginFormView.setVisibility(show ? 8 : 0);
                }
            });
            View view2 = this.mProgressView;
            if (!show) {
                i2 = 8;
            }
            view2.setVisibility(i2);
            ViewPropertyAnimator duration = this.mProgressView.animate().setDuration((long) shortAnimTime);
            if (!show) {
                f = 0.0f;
            }
            duration.alpha(f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    LoginActivity.this.mProgressView.setVisibility(show ? 0 : 8);
                }
            });
            return;
        }
        View view3 = this.mProgressView;
        if (show) {
            i3 = 0;
        } else {
            i3 = 8;
        }
        view3.setVisibility(i3);
        view3 = this.mLoginFormView;
        if (!show) {
            i = 0;
        }
        view3.setVisibility(i);
    }

    public void UserLoginTask(String host, final String username, final String password) {
        TextView mTextView = (TextView) findViewById(R.id.response);
        final String uri = URLUtil.guessUrl(host);
        VolleySingleton.getInstance().addToRequestQueue(new StringRequest(0, uri + "/player_api.php?username=" + username + "&password=" + password, new Listener<String>() {
            public void onResponse(String response) {
                LoginActivity.this.showProgress(false);
                LoginActivity.this.account.save(uri, username, password);
                LoginActivity.this.account.setAuthResponse(response);
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                LoginActivity.this.finish();
            }
        }, new C09896()));
    }
}
