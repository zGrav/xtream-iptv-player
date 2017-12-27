package z.xtreamiptv.player;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import z.xtreamiptv.player.TracksFragment.ITrackHolder;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import widget.media.AndroidMediaController;
import widget.media.IjkVideoView;
import widget.media.MeasureHelper;

public class VideoActivity extends AppCompatActivity implements ITrackHolder {
    private static final String TAG = "VideoActivity";
    private boolean mBackPressed;
    private DrawerLayout mDrawerLayout;
    private TableLayout mHudView;
    private AndroidMediaController mMediaController;
    private ViewGroup mRightDrawer;
    private TextView mToastTextView;
    private String mVideoPath;
    private Uri mVideoUri;
    private IjkVideoView mVideoView;

    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        return intent;
    }

    public static void intentTo(Context context, String videoPath, String videoTitle) {
        context.startActivity(newIntent(context, videoPath, videoTitle));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.activity_player);
        this.mVideoPath = getIntent().getStringExtra("videoPath");
        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction)) {
            if (intentAction.equals("android.intent.action.VIEW")) {
                this.mVideoPath = intent.getDataString();
            } else if (intentAction.equals("android.intent.action.SEND")) {
                this.mVideoUri = (Uri) intent.getParcelableExtra("android.intent.extra.STREAM");
                if (VERSION.SDK_INT < 14) {
                    String scheme = this.mVideoUri.getScheme();
                    if (TextUtils.isEmpty(scheme)) {
                        Log.e(TAG, "Null unknown scheme\n");
                        finish();
                        return;
                    } else if (scheme.equals("android.resource")) {
                        this.mVideoPath = this.mVideoUri.getPath();
                    } else if (scheme.equals(Param.CONTENT)) {
                        Log.e(TAG, "Can not resolve content below Android-ICS\n");
                        finish();
                        return;
                    } else {
                        Log.e(TAG, "Unknown scheme " + scheme + "\n");
                        finish();
                        return;
                    }
                }
            }
        }
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        this.mMediaController = new AndroidMediaController((Context) this, false);
        this.mMediaController.setSupportActionBar(actionBar);
        this.mToastTextView = (TextView) findViewById(R.id.toast_text_view);
        this.mHudView = (TableLayout) findViewById(R.id.hud_view);
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.mRightDrawer = (ViewGroup) findViewById(R.id.right_drawer);
        this.mDrawerLayout.setScrimColor(0);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        this.mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        this.mVideoView.setMediaController(this.mMediaController);
        this.mVideoView.setHudView(this.mHudView);
        if (this.mVideoPath != null) {
            this.mVideoView.setVideoPath(this.mVideoPath);
        } else if (this.mVideoUri != null) {
            this.mVideoView.setVideoURI(this.mVideoUri);
        } else {
            Log.e(TAG, "Null Data Source\n");
            finish();
            return;
        }
        this.mVideoView.start();
    }

    public void onBackPressed() {
        this.mBackPressed = true;
        super.onBackPressed();
    }

    protected void onStop() {
        super.onStop();
        if (this.mBackPressed || !this.mVideoView.isBackgroundPlayEnabled()) {
            this.mVideoView.stopPlayback();
            this.mVideoView.release(true);
            this.mVideoView.stopBackgroundPlay();
        } else {
            this.mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_toggle_ratio) {
            this.mToastTextView.setText(MeasureHelper.getAspectRatioText(this, this.mVideoView.toggleAspectRatio()));
            this.mMediaController.showOnce(this.mToastTextView);
            return true;
        }
        if (id == R.id.action_show_info) {
            this.mVideoView.showMediaInfo();
        } else if (id == R.id.action_show_tracks) {
            Fragment f;
            FragmentTransaction transaction;
            if (this.mDrawerLayout.isDrawerOpen(this.mRightDrawer)) {
                f = getSupportFragmentManager().findFragmentById(R.id.right_drawer);
                if (f != null) {
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.remove(f);
                    transaction.commit();
                }
                this.mDrawerLayout.closeDrawer(this.mRightDrawer);
            } else {
                f = TracksFragment.newInstance();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.right_drawer, f);
                transaction.commit();
                this.mDrawerLayout.openDrawer(this.mRightDrawer);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public ITrackInfo[] getTrackInfo() {
        if (this.mVideoView == null) {
            return null;
        }
        return this.mVideoView.getTrackInfo();
    }

    public void selectTrack(int stream) {
        this.mVideoView.selectTrack(stream);
    }

    public void deselectTrack(int stream) {
        this.mVideoView.deselectTrack(stream);
    }

    public int getSelectedTrack(int trackType) {
        if (this.mVideoView == null) {
            return -1;
        }
        return this.mVideoView.getSelectedTrack(trackType);
    }
}
