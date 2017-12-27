package Models;

public class Movie {
    private String added;
    private String category_id;
    private String container_extension;
    private String custom_sid;
    private String direct_source;
    private String name;
    private int num;
    private int series_no;
    private String stream_icon;
    private int stream_id;
    private String stream_type;

    public String getStream_icon() {
        return this.stream_icon;
    }

    public String getContainer_extension() {
        return this.container_extension;
    }

    public int getStream_id() {
        return this.stream_id;
    }

    public String getName() {
        return this.name;
    }

    public Movie(String name, int stream_id, String stream_icon, String container_extension) {
        this.name = name;
        this.stream_id = stream_id;
        this.stream_icon = stream_icon;
        this.container_extension = container_extension;
    }
}
