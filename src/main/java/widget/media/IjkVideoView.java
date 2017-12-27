package widget.media;

import Settings.Setting;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import z.xtreamiptv.player.R;
import z.xtreamiptv.player.services.MediaPlayerService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnBufferingUpdateListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnSeekCompleteListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnTimedTextListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnVideoSizeChangedListener;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.TextureMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaFormat;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkMediaFormat;
import widget.media.IRenderView.IRenderCallback;
import widget.media.IRenderView.ISurfaceHolder;

public class IjkVideoView extends FrameLayout implements MediaPlayerControl {
    public static final int RENDER_NONE = 0;
    public static final int RENDER_SURFACE_VIEW = 1;
    public static final int RENDER_TEXTURE_VIEW = 2;
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PREPARING = 1;
    private static final int[] s_allAspectRatio = new int[]{0, 1, 2, 4, 5};
    private String TAG = "IjkVideoView";
    private List<Integer> mAllRenders = new ArrayList();
    private Context mAppContext;
    //private OnBufferingUpdateListener mBufferingUpdateListener = new mBufferingUpdateListener();
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;
    private OnCompletionListener mCompletionListener = new C10103();
    private int mCurrentAspectRatio = s_allAspectRatio[0];
    private int mCurrentAspectRatioIndex = 0;
    private int mCurrentBufferPercentage;
    private int mCurrentRender = 0;
    private int mCurrentRenderIndex = 0;
    private int mCurrentState = 0;
    private boolean mEnableBackgroundPlay = false;
    //private OnErrorListener mErrorListener = new mErrorListener();
    private Map<String, String> mHeaders;
    private InfoHudViewHolder mHudViewHolder;
    private OnInfoListener mInfoListener = new C10114();
    private IMediaController mMediaController;
    private IMediaPlayer mMediaPlayer = null;
    private OnCompletionListener mOnCompletionListener;
    private OnErrorListener mOnErrorListener;
    private OnInfoListener mOnInfoListener;
    private OnPreparedListener mOnPreparedListener;
   // private OnTimedTextListener mOnTimedTextListener = new mOnTimedTextListener();
    private long mPrepareEndTime = 0;
    private long mPrepareStartTime = 0;
    OnPreparedListener mPreparedListener = new C10092();
    private IRenderView mRenderView;
    IRenderCallback mSHCallback = new C10169();
    //private OnSeekCompleteListener mSeekCompleteListener = new mSeekCompleteListener();
    private long mSeekEndTime = 0;
    private long mSeekStartTime = 0;
    private int mSeekWhenPrepared;
    private Setting mSettings;
    OnVideoSizeChangedListener mSizeChangedListener = new C10081();
    private int mSurfaceHeight;
    private ISurfaceHolder mSurfaceHolder = null;
    private int mSurfaceWidth;
    private int mTargetState = 0;
    private Uri mUri;
    private int mVideoHeight;
    private int mVideoRotationDegree;
    private int mVideoSarDen;
    private int mVideoSarNum;
    private int mVideoWidth;
    private ProgressBar progressBar;
    private TextView progress_val;
    private TextView subtitleDisplay;

    class C10081 implements OnVideoSizeChangedListener {
        C10081() {
        }

        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
            IjkVideoView.this.mVideoWidth = mp.getVideoWidth();
            IjkVideoView.this.mVideoHeight = mp.getVideoHeight();
            IjkVideoView.this.mVideoSarNum = mp.getVideoSarNum();
            IjkVideoView.this.mVideoSarDen = mp.getVideoSarDen();
            if (IjkVideoView.this.mVideoWidth != 0 && IjkVideoView.this.mVideoHeight != 0) {
                if (IjkVideoView.this.mRenderView != null) {
                    IjkVideoView.this.mRenderView.setVideoSize(IjkVideoView.this.mVideoWidth, IjkVideoView.this.mVideoHeight);
                    IjkVideoView.this.mRenderView.setVideoSampleAspectRatio(IjkVideoView.this.mVideoSarNum, IjkVideoView.this.mVideoSarDen);
                }
                IjkVideoView.this.requestLayout();
            }
        }
    }

    class C10092 implements OnPreparedListener {
        C10092() {
        }

        public void onPrepared(IMediaPlayer mp) {
            IjkVideoView.this.mPrepareEndTime = System.currentTimeMillis();
            IjkVideoView.this.mHudViewHolder.updateLoadCost(IjkVideoView.this.mPrepareEndTime - IjkVideoView.this.mPrepareStartTime);
            IjkVideoView.this.mCurrentState = 2;
            if (IjkVideoView.this.mOnPreparedListener != null) {
                IjkVideoView.this.mOnPreparedListener.onPrepared(IjkVideoView.this.mMediaPlayer);
            }
            if (IjkVideoView.this.mMediaController != null) {
                IjkVideoView.this.mMediaController.setEnabled(true);
            }
            IjkVideoView.this.mVideoWidth = mp.getVideoWidth();
            IjkVideoView.this.mVideoHeight = mp.getVideoHeight();
            int seekToPosition = IjkVideoView.this.mSeekWhenPrepared;
            if (seekToPosition != 0) {
                IjkVideoView.this.seekTo(seekToPosition);
            }
            if (IjkVideoView.this.mVideoWidth == 0 || IjkVideoView.this.mVideoHeight == 0) {
                if (IjkVideoView.this.mTargetState == 3) {
                    IjkVideoView.this.start();
                }
            } else if (IjkVideoView.this.mRenderView != null) {
                IjkVideoView.this.mRenderView.setVideoSize(IjkVideoView.this.mVideoWidth, IjkVideoView.this.mVideoHeight);
                IjkVideoView.this.mRenderView.setVideoSampleAspectRatio(IjkVideoView.this.mVideoSarNum, IjkVideoView.this.mVideoSarDen);
                if (IjkVideoView.this.mRenderView.shouldWaitForResize() && (IjkVideoView.this.mSurfaceWidth != IjkVideoView.this.mVideoWidth || IjkVideoView.this.mSurfaceHeight != IjkVideoView.this.mVideoHeight)) {
                    return;
                }
                if (IjkVideoView.this.mTargetState == 3) {
                    IjkVideoView.this.start();
                    if (IjkVideoView.this.mMediaController != null) {
                        IjkVideoView.this.mMediaController.show();
                    }
                } else if (!IjkVideoView.this.isPlaying()) {
                    if ((seekToPosition != 0 || IjkVideoView.this.getCurrentPosition() > 0) && IjkVideoView.this.mMediaController != null) {
                        IjkVideoView.this.mMediaController.show(0);
                    }
                }
            }
        }
    }

    class C10103 implements OnCompletionListener {
        C10103() {
        }

        public void onCompletion(IMediaPlayer mp) {
            IjkVideoView.this.mCurrentState = 5;
            IjkVideoView.this.mTargetState = 5;
            if (IjkVideoView.this.mMediaController != null) {
                IjkVideoView.this.mMediaController.hide();
            }
            if (IjkVideoView.this.mOnCompletionListener != null) {
                IjkVideoView.this.mOnCompletionListener.onCompletion(IjkVideoView.this.mMediaPlayer);
            }
        }
    }

    class C10114 implements OnInfoListener {
        C10114() {
        }

        public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
            if (IjkVideoView.this.mOnInfoListener != null) {
                IjkVideoView.this.mOnInfoListener.onInfo(mp, arg1, arg2);
            }
            switch (arg1) {
                case 3:
                    Log.d(IjkVideoView.this.TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                    IjkVideoView.this.progressBar.setVisibility(View.INVISIBLE);
                    break;
                case 700:
                    Log.d(IjkVideoView.this.TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START /*701*/:
                    Log.d(IjkVideoView.this.TAG, "MEDIA_INFO_BUFFERING_START:");
                    IjkVideoView.this.progressBar.setVisibility(View.INVISIBLE);
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END /*702*/:
                    Log.d(IjkVideoView.this.TAG, "MEDIA_INFO_BUFFERING_END:");
                    IjkVideoView.this.progressBar.setVisibility(View.VISIBLE);
                    break;
                case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH /*703*/:
                    Log.d(IjkVideoView.this.TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                    break;
                case 800:
                    Log.d(IjkVideoView.this.TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                    break;
                case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE /*801*/:
                    Log.d(IjkVideoView.this.TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                    break;
                case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE /*802*/:
                    Log.d(IjkVideoView.this.TAG, "MEDIA_INFO_METADATA_UPDATE:");
                    break;
                case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE /*901*/:
                    Log.d(IjkVideoView.this.TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                    break;
                case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT /*902*/:
                    Log.d(IjkVideoView.this.TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                    break;
                case 10001:
                    IjkVideoView.this.mVideoRotationDegree = arg2;
                    Log.d(IjkVideoView.this.TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
                    if (IjkVideoView.this.mRenderView != null) {
                        IjkVideoView.this.mRenderView.setVideoRotation(arg2);
                        break;
                    }
                    break;
                case 10002:
                    Log.d(IjkVideoView.this.TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                    break;
            }
            return true;
        }
    }

    /*
            class C06971 implements OnClickListener {
            C06971() {
            }

            public void onClick(DialogInterface dialog, int whichButton) {
                if (IjkVideoView.this.mOnCompletionListener != null) {
                    IjkVideoView.this.mOnCompletionListener.onCompletion(IjkVideoView.this.mMediaPlayer);
                }
            }
     */
    private IMediaPlayer.OnErrorListener mErrorListener =
            new IMediaPlayer.OnErrorListener() {
                public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
                    Log.d(IjkVideoView.this.TAG, "Error: " + framework_err + "," + impl_err);
                    IjkVideoView.this.mCurrentState = -1;
                    IjkVideoView.this.mTargetState = -1;
                    if (IjkVideoView.this.mMediaController != null) {
                        IjkVideoView.this.mMediaController.hide();
                    }
                    if ((IjkVideoView.this.mOnErrorListener == null || !IjkVideoView.this.mOnErrorListener.onError(IjkVideoView.this.mMediaPlayer, framework_err, impl_err)) && IjkVideoView.this.getWindowToken() != null) {
                        int messageId;
                        Resources r = IjkVideoView.this.mAppContext.getResources();
                        if (framework_err == 200) {
                            messageId = R.string.VideoView_error_text_invalid_progressive_playback;
                        } else {
                            messageId = R.string.VideoView_error_text_unknown;
                        }
                        new Builder(IjkVideoView.this.getContext()).setMessage(messageId).setPositiveButton((int) R.string.VideoView_error_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                            /* If we get here, there is no onError listener, so
                                             * at least inform them that the video is over.
                                             */
                                if (mOnCompletionListener != null) {
                                    mOnCompletionListener.onCompletion(mMediaPlayer);
                                }
                            }
                        }).setCancelable(false).show();
                    }
                    return true;
                }

            };

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new IMediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                }
            };

    private IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener =
            new IMediaPlayer.OnSeekCompleteListener() {
                public void onSeekComplete(IMediaPlayer mp) {
                    IjkVideoView.this.mSeekEndTime = System.currentTimeMillis();
                    IjkVideoView.this.mHudViewHolder.updateSeekCost(IjkVideoView.this.mSeekEndTime - IjkVideoView.this.mSeekStartTime);
                }
            };

    private IMediaPlayer.OnTimedTextListener mOnTimedTextListener =
            new IMediaPlayer.OnTimedTextListener() {
                public void onTimedText(IMediaPlayer mp, IjkTimedText text) {
                    if (text != null) {
                        IjkVideoView.this.subtitleDisplay.setText(text.getText());
                    }
                }
            };

    class C10169 implements IRenderCallback {
        C10169() {
        }

        public void onSurfaceChanged(@NonNull ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != IjkVideoView.this.mRenderView) {
                Log.e(IjkVideoView.this.TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }
            IjkVideoView.this.mSurfaceWidth = w;
            IjkVideoView.this.mSurfaceHeight = h;
            boolean isValidState;
            if (IjkVideoView.this.mTargetState == 3) {
                isValidState = true;
            } else {
                isValidState = false;
            }
            boolean hasValidSize;
            if (!IjkVideoView.this.mRenderView.shouldWaitForResize() || (IjkVideoView.this.mVideoWidth == w && IjkVideoView.this.mVideoHeight == h)) {
                hasValidSize = true;
            } else {
                hasValidSize = false;
            }
            if (IjkVideoView.this.mMediaPlayer != null && isValidState && hasValidSize) {
                if (IjkVideoView.this.mSeekWhenPrepared != 0) {
                    IjkVideoView.this.seekTo(IjkVideoView.this.mSeekWhenPrepared);
                }
                IjkVideoView.this.start();
            }
        }

        public void onSurfaceCreated(@NonNull ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != IjkVideoView.this.mRenderView) {
                Log.e(IjkVideoView.this.TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }
            IjkVideoView.this.mSurfaceHolder = holder;
            if (IjkVideoView.this.mMediaPlayer != null) {
                IjkVideoView.this.bindSurfaceHolder(IjkVideoView.this.mMediaPlayer, holder);
            } else {
                IjkVideoView.this.openVideo();
            }
        }

        public void onSurfaceDestroyed(@NonNull ISurfaceHolder holder) {
            if (holder.getRenderView() != IjkVideoView.this.mRenderView) {
                Log.e(IjkVideoView.this.TAG, "onSurfaceDestroyed: unmatched render callback\n");
                return;
            }
            IjkVideoView.this.mSurfaceHolder = null;
            IjkVideoView.this.releaseWithoutStop();
        }
    }

    public IjkVideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    @TargetApi(21)
    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }

    private void initVideoView(Context context) {
        this.mAppContext = context.getApplicationContext();
        this.mSettings = new Setting(this.mAppContext);
        initBackground();
        initRenders();
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        this.mCurrentState = 0;
        this.mTargetState = 0;
        this.subtitleDisplay = new TextView(context);
        this.subtitleDisplay.setTextSize(24.0f);
        this.subtitleDisplay.setGravity(17);
        addView(this.subtitleDisplay, new LayoutParams(-1, -2, 80));
        LayoutParams layoutParams_pBar = new LayoutParams(-1, -2, 17);
        this.progressBar = new ProgressBar(context);
        this.progressBar.setIndeterminate(true);
        addView(this.progressBar, layoutParams_pBar);
        this.progressBar.getLayoutParams().height = 200;
    }

    public void setRenderView(IRenderView renderView) {
        if (this.mRenderView != null) {
            if (this.mMediaPlayer != null) {
                this.mMediaPlayer.setDisplay(null);
            }
            View renderUIView = this.mRenderView.getView();
            this.mRenderView.removeRenderCallback(this.mSHCallback);
            this.mRenderView = null;
            removeView(renderUIView);
        }
        if (renderView != null) {
            this.mRenderView = renderView;
            renderView.setAspectRatio(this.mCurrentAspectRatio);
            if (this.mVideoWidth > 0 && this.mVideoHeight > 0) {
                renderView.setVideoSize(this.mVideoWidth, this.mVideoHeight);
            }
            if (this.mVideoSarNum > 0 && this.mVideoSarDen > 0) {
                renderView.setVideoSampleAspectRatio(this.mVideoSarNum, this.mVideoSarDen);
            }
            View renderUIView = this.mRenderView.getView();
            renderUIView.setLayoutParams(new LayoutParams(-2, -2, 17));
            addView(renderUIView);
            this.mRenderView.addRenderCallback(this.mSHCallback);
            this.mRenderView.setVideoRotation(this.mVideoRotationDegree);
        }
    }

    public void setRender(int render) {
        switch (render) {
            case 0:
                setRenderView(null);
                return;
            case 1:
                setRenderView(new SurfaceRenderView(getContext()));
                return;
            case 2:
                TextureRenderView renderView = new TextureRenderView(getContext());
                if (this.mMediaPlayer != null) {
                    renderView.getSurfaceHolder().bindToMediaPlayer(this.mMediaPlayer);
                    renderView.setVideoSize(this.mMediaPlayer.getVideoWidth(), this.mMediaPlayer.getVideoHeight());
                    renderView.setVideoSampleAspectRatio(this.mMediaPlayer.getVideoSarNum(), this.mMediaPlayer.getVideoSarDen());
                    renderView.setAspectRatio(this.mCurrentAspectRatio);
                }
                setRenderView(renderView);
                return;
            default:
                Log.e(this.TAG, String.format(Locale.getDefault(), "invalid render %d\n", new Object[]{Integer.valueOf(render)}));
                return;
        }
    }

    public void setHudView(TableLayout tableLayout) {
        this.mHudViewHolder = new InfoHudViewHolder(getContext(), tableLayout);
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    private void setVideoURI(Uri uri, Map<String, String> headers) {
        this.mUri = uri;
        this.mHeaders = headers;
        this.mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void stopPlayback() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            if (this.mHudViewHolder != null) {
                this.mHudViewHolder.setMediaPlayer(null);
            }
            this.mCurrentState = 0;
            this.mTargetState = 0;
            ((AudioManager) this.mAppContext.getSystemService("audio")).abandonAudioFocus(null);
        }
    }

    @TargetApi(23)
    private void openVideo() {
        if (this.mUri != null && this.mSurfaceHolder != null) {
            release(false);
            ((AudioManager) this.mAppContext.getSystemService("audio")).requestAudioFocus(null, 3, 1);
            try {
                this.mMediaPlayer = createPlayer(this.mSettings.getPlayer());
                Context context = getContext();
                this.mMediaPlayer.setOnPreparedListener(this.mPreparedListener);
                this.mMediaPlayer.setOnVideoSizeChangedListener(this.mSizeChangedListener);
                this.mMediaPlayer.setOnCompletionListener(this.mCompletionListener);
                this.mMediaPlayer.setOnErrorListener(this.mErrorListener);
                this.mMediaPlayer.setOnInfoListener(this.mInfoListener);
                this.mMediaPlayer.setOnBufferingUpdateListener(this.mBufferingUpdateListener);
                this.mMediaPlayer.setOnSeekCompleteListener(this.mSeekCompleteListener);
                this.mMediaPlayer.setOnTimedTextListener(this.mOnTimedTextListener);
                this.mCurrentBufferPercentage = 0;
                String scheme = this.mUri.getScheme();
                if (VERSION.SDK_INT >= 23 && this.mSettings.getUsingMediaDataSource() && (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
                    this.mMediaPlayer.setDataSource(new FileMediaDataSource(new File(this.mUri.toString())));
                } else if (VERSION.SDK_INT >= 14) {
                    this.mMediaPlayer.setDataSource(this.mAppContext, this.mUri, this.mHeaders);
                } else {
                    this.mMediaPlayer.setDataSource(this.mUri.toString());
                }
                bindSurfaceHolder(this.mMediaPlayer, this.mSurfaceHolder);
                this.mMediaPlayer.setAudioStreamType(3);
                this.mMediaPlayer.setScreenOnWhilePlaying(true);
                this.mPrepareStartTime = System.currentTimeMillis();
                this.mMediaPlayer.prepareAsync();
                if (this.mHudViewHolder != null) {
                    this.mHudViewHolder.setMediaPlayer(this.mMediaPlayer);
                }
                this.mCurrentState = 1;
                attachMediaController();
            } catch (IOException ex) {
                Log.w(this.TAG, "Unable to open content: " + this.mUri, ex);
                this.mCurrentState = -1;
                this.mTargetState = -1;
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
            } catch (IllegalArgumentException ex2) {
                Log.w(this.TAG, "Unable to open content: " + this.mUri, ex2);
                this.mCurrentState = -1;
                this.mTargetState = -1;
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
            }
        }
    }

    public void setMediaController(IMediaController controller) {
        if (this.mMediaController != null) {
            this.mMediaController.hide();
        }
        this.mMediaController = controller;
        attachMediaController();
    }

    private void attachMediaController() {
        if (this.mMediaPlayer != null && this.mMediaController != null) {
            View anchorView;
            this.mMediaController.setMediaPlayer(this);
            if (getParent() instanceof View) {
                anchorView = (View) getParent();
            } else {
                anchorView = this;
            }
            this.mMediaController.setAnchorView(anchorView);
            this.mMediaController.setEnabled(isInPlaybackState());
        }
    }

    public void setOnPreparedListener(OnPreparedListener l) {
        this.mOnPreparedListener = l;
    }

    public void setOnCompletionListener(OnCompletionListener l) {
        this.mOnCompletionListener = l;
    }

    public void setOnErrorListener(OnErrorListener l) {
        this.mOnErrorListener = l;
    }

    public void setOnInfoListener(OnInfoListener l) {
        this.mOnInfoListener = l;
    }

    private void bindSurfaceHolder(IMediaPlayer mp, ISurfaceHolder holder) {
        if (mp != null) {
            if (holder == null) {
                mp.setDisplay(null);
            } else {
                holder.bindToMediaPlayer(mp);
            }
        }
    }

    public void releaseWithoutStop() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.setDisplay(null);
        }
    }

    public void release(boolean cleartargetstate) {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mCurrentState = 0;
            if (cleartargetstate) {
                this.mTargetState = 0;
            }
            ((AudioManager) this.mAppContext.getSystemService("audio")).abandonAudioFocus(null);
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && this.mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    public boolean onTrackballEvent(MotionEvent ev) {
        if (isInPlaybackState() && this.mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = (keyCode == 4 || keyCode == 24 || keyCode == 25 || keyCode == 164 || keyCode == 82 || keyCode == 5 || keyCode == 6) ? false : true;
        if (isInPlaybackState() && isKeyCodeSupported && this.mMediaController != null) {
            if (keyCode == 79 || keyCode == 85) {
                if (this.mMediaPlayer.isPlaying()) {
                    pause();
                    this.mMediaController.show();
                    return true;
                }
                start();
                this.mMediaController.hide();
                return true;
            } else if (keyCode == 126) {
                if (this.mMediaPlayer.isPlaying()) {
                    return true;
                }
                start();
                this.mMediaController.hide();
                return true;
            } else if (keyCode != 86 && keyCode != 127) {
                toggleMediaControlsVisiblity();
            } else if (!this.mMediaPlayer.isPlaying()) {
                return true;
            } else {
                pause();
                this.mMediaController.show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (this.mMediaController.isShowing()) {
            this.mMediaController.hide();
        } else {
            this.mMediaController.show();
        }
    }

    public void start() {
        if (isInPlaybackState()) {
            this.mMediaPlayer.start();
            this.mCurrentState = 3;
        }
        this.mTargetState = 3;
    }

    public void pause() {
        if (isInPlaybackState() && this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
            this.mCurrentState = 4;
        }
        this.mTargetState = 4;
    }

    public void suspend() {
        release(false);
    }

    public void resume() {
        openVideo();
    }

    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) this.mMediaPlayer.getDuration();
        }
        return -1;
    }

    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) this.mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            this.mSeekStartTime = System.currentTimeMillis();
            this.mMediaPlayer.seekTo((long) msec);
            this.mSeekWhenPrepared = 0;
            return;
        }
        this.mSeekWhenPrepared = msec;
    }

    public boolean isPlaying() {
        return isInPlaybackState() && this.mMediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        if (this.mMediaPlayer != null) {
            return this.mCurrentBufferPercentage;
        }
        return 0;
    }

    private boolean isInPlaybackState() {
        return (this.mMediaPlayer == null || this.mCurrentState == -1 || this.mCurrentState == 0 || this.mCurrentState == 1) ? false : true;
    }

    public boolean canPause() {
        return this.mCanPause;
    }

    public boolean canSeekBackward() {
        return this.mCanSeekBack;
    }

    public boolean canSeekForward() {
        return this.mCanSeekForward;
    }

    public int getAudioSessionId() {
        return 0;
    }

    public int toggleAspectRatio() {
        this.mCurrentAspectRatioIndex++;
        this.mCurrentAspectRatioIndex %= s_allAspectRatio.length;
        this.mCurrentAspectRatio = s_allAspectRatio[this.mCurrentAspectRatioIndex];
        if (this.mRenderView != null) {
            this.mRenderView.setAspectRatio(this.mCurrentAspectRatio);
        }
        return this.mCurrentAspectRatio;
    }

    private void initRenders() {
        this.mAllRenders.clear();
        if (this.mSettings.getEnableSurfaceView()) {
            this.mAllRenders.add(Integer.valueOf(1));
        }
        if (this.mSettings.getEnableTextureView() && VERSION.SDK_INT >= 14) {
            this.mAllRenders.add(Integer.valueOf(2));
        }
        if (this.mSettings.getEnableNoView()) {
            this.mAllRenders.add(Integer.valueOf(0));
        }
        if (this.mAllRenders.isEmpty()) {
            this.mAllRenders.add(Integer.valueOf(1));
        }
        this.mCurrentRender = ((Integer) this.mAllRenders.get(this.mCurrentRenderIndex)).intValue();
        setRender(this.mCurrentRender);
    }

    public int toggleRender() {
        this.mCurrentRenderIndex++;
        this.mCurrentRenderIndex %= this.mAllRenders.size();
        this.mCurrentRender = ((Integer) this.mAllRenders.get(this.mCurrentRenderIndex)).intValue();
        setRender(this.mCurrentRender);
        return this.mCurrentRender;
    }

    @NonNull
    public static String getRenderText(Context context, int render) {
        switch (render) {
            case 0:
                return context.getString(R.string.VideoView_render_none);
            case 1:
                return context.getString(R.string.VideoView_render_surface_view);
            case 2:
                return context.getString(R.string.VideoView_render_texture_view);
            default:
                return context.getString(R.string.N_A);
        }
    }

    public int togglePlayer() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.release();
        }
        if (this.mRenderView != null) {
            this.mRenderView.getView().invalidate();
        }
        openVideo();
        return this.mSettings.getPlayer();
    }

    @NonNull
    public static String getPlayerText(Context context, int player) {
        switch (player) {
            case 1:
                return context.getString(R.string.VideoView_player_AndroidMediaPlayer);
            case 2:
                return context.getString(R.string.VideoView_player_IjkMediaPlayer);
            case 3:
                return context.getString(R.string.VideoView_player_IjkExoMediaPlayer);
            default:
                return context.getString(R.string.N_A);
        }
    }

    public IMediaPlayer createPlayer(int playerType) {
        IMediaPlayer mediaPlayer2 = null;
        switch (playerType) {
            case 1:
                Object mediaPlayer = new AndroidMediaPlayer();
                break;
            default:
                IjkMediaPlayer ijkMediaPlayer = null;
                if (this.mUri != null) {
                    ijkMediaPlayer = new IjkMediaPlayer();
                    IjkMediaPlayer.native_setLogLevel(3);
                    if (this.mSettings.getUsingMediaCodec()) {
                        ijkMediaPlayer.setOption(4, "mediacodec", 1);
                        if (this.mSettings.getUsingMediaCodecAutoRotate()) {
                            ijkMediaPlayer.setOption(4, "mediacodec-auto-rotate", 1);
                        } else {
                            ijkMediaPlayer.setOption(4, "mediacodec-auto-rotate", 0);
                        }
                        if (this.mSettings.getMediaCodecHandleResolutionChange()) {
                            ijkMediaPlayer.setOption(4, "mediacodec-handle-resolution-change", 1);
                        } else {
                            ijkMediaPlayer.setOption(4, "mediacodec-handle-resolution-change", 0);
                        }
                    } else {
                        ijkMediaPlayer.setOption(4, "mediacodec", 0);
                    }
                    if (this.mSettings.getUsingOpenSLES()) {
                        ijkMediaPlayer.setOption(4, "opensles", 1);
                    } else {
                        ijkMediaPlayer.setOption(4, "opensles", 0);
                    }
                    String pixelFormat = this.mSettings.getPixelFormat();
                    if (TextUtils.isEmpty(pixelFormat)) {
                        ijkMediaPlayer.setOption(4, "overlay-format", 842225234);
                    } else {
                        ijkMediaPlayer.setOption(4, "overlay-format", pixelFormat);
                    }
                    ijkMediaPlayer.setOption(4, "framedrop", 1);
                    ijkMediaPlayer.setOption(4, "start-on-prepared", 0);
                    ijkMediaPlayer.setOption(1, "http-detect-range-support", 0);
                    ijkMediaPlayer.setOption(2, "skip_loop_filter", 48);
                }
                mediaPlayer2 = ijkMediaPlayer;
                break;
        }
        if (this.mSettings.getEnableDetachedSurfaceTextureView()) {
            return new TextureMediaPlayer(mediaPlayer2);
        }
        return mediaPlayer2;
    }

    private void initBackground() {
        this.mEnableBackgroundPlay = this.mSettings.getEnableBackgroundPlay();
        if (this.mEnableBackgroundPlay) {
            MediaPlayerService.intentToStart(getContext());
            this.mMediaPlayer = MediaPlayerService.getMediaPlayer();
            if (this.mHudViewHolder != null) {
                this.mHudViewHolder.setMediaPlayer(this.mMediaPlayer);
            }
        }
    }

    public boolean isBackgroundPlayEnabled() {
        return this.mEnableBackgroundPlay;
    }

    public void enterBackground() {
        MediaPlayerService.setMediaPlayer(this.mMediaPlayer);
    }

    public void stopBackgroundPlay() {
        MediaPlayerService.setMediaPlayer(null);
    }

    public void showMediaInfo() {
        if (this.mMediaPlayer != null) {
            int selectedVideoTrack = MediaPlayerCompat.getSelectedTrack(this.mMediaPlayer, 1);
            int selectedAudioTrack = MediaPlayerCompat.getSelectedTrack(this.mMediaPlayer, 2);
            int selectedSubtitleTrack = MediaPlayerCompat.getSelectedTrack(this.mMediaPlayer, 3);
            TableLayoutBinder builder = new TableLayoutBinder(getContext());
            builder.appendSection((int) R.string.mi_player);
            builder.appendRow2((int) R.string.mi_player, MediaPlayerCompat.getName(this.mMediaPlayer));
            builder.appendSection((int) R.string.mi_media);
            builder.appendRow2((int) R.string.mi_resolution, buildResolution(this.mVideoWidth, this.mVideoHeight, this.mVideoSarNum, this.mVideoSarDen));
            builder.appendRow2((int) R.string.mi_length, buildTimeMilli(this.mMediaPlayer.getDuration()));
            ITrackInfo[] trackInfos = this.mMediaPlayer.getTrackInfo();
            if (trackInfos != null) {
                int index = -1;
                for (ITrackInfo trackInfo : trackInfos) {
                    index++;
                    int trackType = trackInfo.getTrackType();
                    if (index == selectedVideoTrack) {
                        builder.appendSection(getContext().getString(R.string.mi_stream_fmt1, new Object[]{Integer.valueOf(index)}) + " " + getContext().getString(R.string.mi__selected_video_track));
                    } else if (index == selectedAudioTrack) {
                        builder.appendSection(getContext().getString(R.string.mi_stream_fmt1, new Object[]{Integer.valueOf(index)}) + " " + getContext().getString(R.string.mi__selected_audio_track));
                    } else if (index == selectedSubtitleTrack) {
                        builder.appendSection(getContext().getString(R.string.mi_stream_fmt1, new Object[]{Integer.valueOf(index)}) + " " + getContext().getString(R.string.mi__selected_subtitle_track));
                    } else {
                        builder.appendSection(getContext().getString(R.string.mi_stream_fmt1, new Object[]{Integer.valueOf(index)}));
                    }
                    builder.appendRow2((int) R.string.mi_type, buildTrackType(trackType));
                    builder.appendRow2((int) R.string.mi_language, buildLanguage(trackInfo.getLanguage()));
                    IMediaFormat mediaFormat = trackInfo.getFormat();
                    if (mediaFormat != null && (mediaFormat instanceof IjkMediaFormat)) {
                        switch (trackType) {
                            case 1:
                                builder.appendRow2((int) R.string.mi_codec, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_LONG_NAME_UI));
                                builder.appendRow2((int) R.string.mi_profile_level, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PROFILE_LEVEL_UI));
                                builder.appendRow2((int) R.string.mi_pixel_format, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PIXEL_FORMAT_UI));
                                builder.appendRow2((int) R.string.mi_resolution, mediaFormat.getString(IjkMediaFormat.KEY_IJK_RESOLUTION_UI));
                                builder.appendRow2((int) R.string.mi_frame_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_FRAME_RATE_UI));
                                builder.appendRow2((int) R.string.mi_bit_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_BIT_RATE_UI));
                                break;
                            case 2:
                                builder.appendRow2((int) R.string.mi_codec, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_LONG_NAME_UI));
                                builder.appendRow2((int) R.string.mi_profile_level, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PROFILE_LEVEL_UI));
                                builder.appendRow2((int) R.string.mi_sample_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_SAMPLE_RATE_UI));
                                builder.appendRow2((int) R.string.mi_channels, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CHANNEL_UI));
                                builder.appendRow2((int) R.string.mi_bit_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_BIT_RATE_UI));
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            Builder adBuilder = builder.buildAlertDialogBuilder();
            adBuilder.setTitle((int) R.string.media_information);
            adBuilder.setNegativeButton((int) R.string.close, null);
            adBuilder.show();
        }
    }

    private String buildResolution(int width, int height, int sarNum, int sarDen) {
        StringBuilder sb = new StringBuilder();
        sb.append(width);
        sb.append(" x ");
        sb.append(height);
        if (sarNum > 1 || sarDen > 1) {
            sb.append("[");
            sb.append(sarNum);
            sb.append(":");
            sb.append(sarDen);
            sb.append("]");
        }
        return sb.toString();
    }

    private String buildTimeMilli(long duration) {
        long total_seconds = duration / 1000;
        long hours = total_seconds / 3600;
        long minutes = (total_seconds % 3600) / 60;
        long seconds = total_seconds % 60;
        if (duration <= 0) {
            return "--:--";
        }
        if (hours >= 100) {
            return String.format(Locale.US, "%d:%02d:%02d", new Object[]{Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)});
        } else if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", new Object[]{Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)});
        } else {
            return String.format(Locale.US, "%02d:%02d", new Object[]{Long.valueOf(minutes), Long.valueOf(seconds)});
        }
    }

    private String buildTrackType(int type) {
        Context context = getContext();
        switch (type) {
            case 1:
                return context.getString(R.string.TrackType_video);
            case 2:
                return context.getString(R.string.TrackType_audio);
            case 3:
                return context.getString(R.string.TrackType_timedtext);
            case 4:
                return context.getString(R.string.TrackType_subtitle);
            case 5:
                return context.getString(R.string.TrackType_metadata);
            default:
                return context.getString(R.string.TrackType_unknown);
        }
    }

    private String buildLanguage(String language) {
        if (TextUtils.isEmpty(language)) {
            return "und";
        }
        return language;
    }

    public ITrackInfo[] getTrackInfo() {
        if (this.mMediaPlayer == null) {
            return null;
        }
        return this.mMediaPlayer.getTrackInfo();
    }

    public void selectTrack(int stream) {
        MediaPlayerCompat.selectTrack(this.mMediaPlayer, stream);
    }

    public void deselectTrack(int stream) {
        MediaPlayerCompat.deselectTrack(this.mMediaPlayer, stream);
    }

    public int getSelectedTrack(int trackType) {
        return MediaPlayerCompat.getSelectedTrack(this.mMediaPlayer, trackType);
    }
}
