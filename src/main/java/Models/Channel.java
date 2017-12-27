package Models;

public class Channel {
    private String added;
    private String category_id;
    private String custom_sid;
    private String direct_source;
    private String epg_channel_id;
    private Epg epg_data = new Epg("", "", "", "", "");
    private String name;
    private int num;
    private String stream_icon;
    private int stream_id;
    private String stream_type;
    private int tv_archive;
    private int tv_archive_duration;

    public int getTv_archive() {
        return this.tv_archive;
    }

    public int getTv_archive_duration() {
        return this.tv_archive_duration;
    }

    public void setEpg_data(Epg epg_data) {
        this.epg_data = epg_data;
    }

    public int getStream_id() {
        return this.stream_id;
    }

    public Channel(String name, int stream_id, String stream_icon, String epg_channel_id) {
        this.name = name;
        this.stream_id = stream_id;
        this.stream_icon = stream_icon;
        this.epg_channel_id = epg_channel_id;
    }

    public Channel(String name, int stream_id, String stream_icon, String epg_channel_id, int tv_archive, int tv_archive_duration) {
        this.name = name;
        this.stream_id = stream_id;
        this.stream_icon = stream_icon;
        this.epg_channel_id = epg_channel_id;
        this.tv_archive = tv_archive;
        this.tv_archive_duration = tv_archive_duration;
    }

    public String getName() {
        return this.name;
    }

    public String getStream_icon() {
        return this.stream_icon;
    }

    public String getEpg_channel_id() {
        return this.epg_channel_id;
    }

    public Epg getEpgData() {
        return this.epg_data;
    }
}
