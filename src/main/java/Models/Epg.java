package Models;

import android.text.TextUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Epg {
    private String channel_id;
    private String description;
    private String end;
    private String epg_id;
    private String epg_title;
    private String id;
    private String lang;
    private String start;
    private String start_timestamp;
    private String stop_timestamp;

    public int get_duration() {
        try {
            return (Integer.parseInt(this.stop_timestamp) / 60) - (Integer.parseInt(this.start_timestamp) / 60);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getReplay_time() {
        DateFormat xtreamDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startReplay = "";
        try {
            startReplay = new SimpleDateFormat("yyyy-MM-dd:HH-mm").format(xtreamDateFormat.parse(this.start));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startReplay;
    }

    public String get_date() {
        DateFormat xtreamDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = "";
        try {
            return new SimpleDateFormat("yyyy-MM-dd").format(xtreamDateFormat.parse(this.start));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Epg(String epg_title, String start, String end, String description, String epg_id) {
        this.epg_title = epg_title;
        this.start = start;
        this.end = end;
        this.description = description;
        this.epg_id = epg_id;
    }

    public Epg(String epg_title, String start, String end, String description, String epg_id, String start_timestamp, String stop_timestamp) {
        this.epg_title = epg_title;
        this.start = start;
        this.end = end;
        this.description = description;
        this.epg_id = epg_id;
        this.start_timestamp = start_timestamp;
        this.stop_timestamp = stop_timestamp;
    }

    public String getTitle() {
        return this.epg_title;
    }

    public String getId() {
        return this.epg_id;
    }

    public String getDesc() {
        return this.description;
    }

    public String getStart() {
        if (TextUtils.isEmpty(this.start)) {
            return "";
        }
        DateFormat xtreamDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String prgStart = "";
        try {
            return new SimpleDateFormat("HH:mm").format(xtreamDateFormat.parse(this.start));
        } catch (ParseException e) {
            e.printStackTrace();
            return prgStart;
        }
    }

    public String getEnd() {
        if (TextUtils.isEmpty(this.end)) {
            return "";
        }
        DateFormat xtreamDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String prgStop = "";
        try {
            return new SimpleDateFormat("HH:mm").format(xtreamDateFormat.parse(this.end));
        } catch (ParseException e) {
            e.printStackTrace();
            return prgStop;
        }
    }
}
