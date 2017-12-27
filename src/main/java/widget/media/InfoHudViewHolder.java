package widget.media;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.TableLayout;
import com.xtream_iptv.player.R;
import java.util.Locale;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;

public class InfoHudViewHolder {
    private static final int MSG_UPDATE_HUD = 1;
    private Handler mHandler = new C06981();
    private long mLoadCost = 0;
    private IMediaPlayer mMediaPlayer;
    private SparseArray<View> mRowMap = new SparseArray();
    private long mSeekCost = 0;
    private TableLayoutBinder mTableLayoutBinder;

    class C06981 extends Handler {
        C06981() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    InfoHudViewHolder holder = InfoHudViewHolder.this;
                    IjkMediaPlayer mp = null;
                    if (InfoHudViewHolder.this.mMediaPlayer != null) {
                        if (InfoHudViewHolder.this.mMediaPlayer instanceof IjkMediaPlayer) {
                            mp = (IjkMediaPlayer) InfoHudViewHolder.this.mMediaPlayer;
                        } else if (InfoHudViewHolder.this.mMediaPlayer instanceof MediaPlayerProxy) {
                            IMediaPlayer internal = ((MediaPlayerProxy) InfoHudViewHolder.this.mMediaPlayer).getInternalMediaPlayer();
                            if (internal != null && (internal instanceof IjkMediaPlayer)) {
                                mp = (IjkMediaPlayer) internal;
                            }
                        }
                        if (mp != null) {
                            switch (mp.getVideoDecoder()) {
                                case 1:
                                    InfoHudViewHolder.this.setRowValue(R.string.vdec, "avcodec");
                                    break;
                                case 2:
                                    InfoHudViewHolder.this.setRowValue(R.string.vdec, "MediaCodec");
                                    break;
                                default:
                                    InfoHudViewHolder.this.setRowValue(R.string.vdec, "");
                                    break;
                            }
                            float fpsOutput = mp.getVideoOutputFramesPerSecond();
                            float fpsDecode = mp.getVideoDecodeFramesPerSecond();
                            InfoHudViewHolder.this.setRowValue(R.string.fps, String.format(Locale.US, "%.2f / %.2f", new Object[]{Float.valueOf(fpsDecode), Float.valueOf(fpsOutput)}));
                            long videoCachedDuration = mp.getVideoCachedDuration();
                            long audioCachedDuration = mp.getAudioCachedDuration();
                            long videoCachedBytes = mp.getVideoCachedBytes();
                            long audioCachedBytes = mp.getAudioCachedBytes();
                            long tcpSpeed = mp.getTcpSpeed();
                            long bitRate = mp.getBitRate();
                            long seekLoadDuration = mp.getSeekLoadDuration();
                            InfoHudViewHolder.this.setRowValue(R.string.v_cache, String.format(Locale.US, "%s, %s", new Object[]{InfoHudViewHolder.formatedDurationMilli(videoCachedDuration), InfoHudViewHolder.formatedSize(videoCachedBytes)}));
                            InfoHudViewHolder.this.setRowValue(R.string.a_cache, String.format(Locale.US, "%s, %s", new Object[]{InfoHudViewHolder.formatedDurationMilli(audioCachedDuration), InfoHudViewHolder.formatedSize(audioCachedBytes)}));
                            InfoHudViewHolder.this.setRowValue(R.string.load_cost, String.format(Locale.US, "%d ms", new Object[]{Long.valueOf(InfoHudViewHolder.this.mLoadCost)}));
                            InfoHudViewHolder.this.setRowValue(R.string.seek_cost, String.format(Locale.US, "%d ms", new Object[]{Long.valueOf(InfoHudViewHolder.this.mSeekCost)}));
                            InfoHudViewHolder.this.setRowValue(R.string.seek_load_cost, String.format(Locale.US, "%d ms", new Object[]{Long.valueOf(seekLoadDuration)}));
                            InfoHudViewHolder.this.setRowValue(R.string.tcp_speed, String.format(Locale.US, "%s", new Object[]{InfoHudViewHolder.formatedSpeed(tcpSpeed, 1000)}));
                            InfoHudViewHolder.this.setRowValue(R.string.bit_rate, String.format(Locale.US, "%.2f kbs", new Object[]{Float.valueOf(((float) bitRate) / 1000.0f)}));
                            InfoHudViewHolder.this.mHandler.removeMessages(1);
                            InfoHudViewHolder.this.mHandler.sendEmptyMessageDelayed(1, 500);
                            return;
                        }
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    public InfoHudViewHolder(Context context, TableLayout tableLayout) {
        this.mTableLayoutBinder = new TableLayoutBinder(context, tableLayout);
    }

    private void appendSection(int nameId) {
        this.mTableLayoutBinder.appendSection(nameId);
    }

    private void appendRow(int nameId) {
        this.mRowMap.put(nameId, this.mTableLayoutBinder.appendRow2(nameId, null));
    }

    private void setRowValue(int id, String value) {
        View rowView = (View) this.mRowMap.get(id);
        if (rowView == null) {
            this.mRowMap.put(id, this.mTableLayoutBinder.appendRow2(id, value));
            return;
        }
        this.mTableLayoutBinder.setValueText(rowView, value);
    }

    public void setMediaPlayer(IMediaPlayer mp) {
        this.mMediaPlayer = mp;
        if (this.mMediaPlayer != null) {
            this.mHandler.sendEmptyMessageDelayed(1, 500);
        } else {
            this.mHandler.removeMessages(1);
        }
    }

    private static String formatedDurationMilli(long duration) {
        if (duration >= 1000) {
            return String.format(Locale.US, "%.2f sec", new Object[]{Float.valueOf(((float) duration) / 1000.0f)});
        }
        return String.format(Locale.US, "%d msec", new Object[]{Long.valueOf(duration)});
    }

    private static String formatedSpeed(long bytes, long elapsed_milli) {
        if (elapsed_milli <= 0) {
            return "0 B/s";
        }
        if (bytes <= 0) {
            return "0 B/s";
        }
        float bytes_per_sec = (((float) bytes) * 1000.0f) / ((float) elapsed_milli);
        if (bytes_per_sec >= 1000000.0f) {
            return String.format(Locale.US, "%.2f MB/s", new Object[]{Float.valueOf((bytes_per_sec / 1000.0f) / 1000.0f)});
        } else if (bytes_per_sec >= 1000.0f) {
            return String.format(Locale.US, "%.1f KB/s", new Object[]{Float.valueOf(bytes_per_sec / 1000.0f)});
        } else {
            return String.format(Locale.US, "%d B/s", new Object[]{Long.valueOf((long) bytes_per_sec)});
        }
    }

    public void updateLoadCost(long time) {
        this.mLoadCost = time;
    }

    public void updateSeekCost(long time) {
        this.mSeekCost = time;
    }

    private static String formatedSize(long bytes) {
        if (bytes >= 100000) {
            return String.format(Locale.US, "%.2f MB", new Object[]{Float.valueOf((((float) bytes) / 1000.0f) / 1000.0f)});
        } else if (bytes >= 100) {
            return String.format(Locale.US, "%.1f KB", new Object[]{Float.valueOf(((float) bytes) / 1000.0f)});
        } else {
            return String.format(Locale.US, "%d B", new Object[]{Long.valueOf(bytes)});
        }
    }
}
