package tv.danmaku.ijk.media.player.pragma;

import android.util.Log;
import java.util.Locale;

public class DebugLog {
    public static final boolean ENABLE_DEBUG = true;
    public static final boolean ENABLE_ERROR = true;
    public static final boolean ENABLE_INFO = true;
    public static final boolean ENABLE_VERBOSE = true;
    public static final boolean ENABLE_WARN = true;

    public static void m790e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void m791e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }

    public static void efmt(String tag, String fmt, Object... args) {
        Log.e(tag, String.format(Locale.US, fmt, args));
    }

    public static void m792i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void m793i(String tag, String msg, Throwable tr) {
        Log.i(tag, msg, tr);
    }

    public static void ifmt(String tag, String fmt, Object... args) {
        Log.i(tag, String.format(Locale.US, fmt, args));
    }

    public static void m796w(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void m797w(String tag, String msg, Throwable tr) {
        Log.w(tag, msg, tr);
    }

    public static void wfmt(String tag, String fmt, Object... args) {
        Log.w(tag, String.format(Locale.US, fmt, args));
    }

    public static void m788d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void m789d(String tag, String msg, Throwable tr) {
        Log.d(tag, msg, tr);
    }

    public static void dfmt(String tag, String fmt, Object... args) {
        Log.d(tag, String.format(Locale.US, fmt, args));
    }

    public static void m794v(String tag, String msg) {
        Log.v(tag, msg);
    }

    public static void m795v(String tag, String msg, Throwable tr) {
        Log.v(tag, msg, tr);
    }

    public static void vfmt(String tag, String fmt, Object... args) {
        Log.v(tag, String.format(Locale.US, fmt, args));
    }

    public static void printStackTrace(Throwable e) {
        e.printStackTrace();
    }

    public static void printCause(Throwable e) {
        Throwable cause = e.getCause();
        if (cause != null) {
            e = cause;
        }
        printStackTrace(e);
    }
}
