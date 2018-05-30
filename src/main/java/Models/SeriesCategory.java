package Models;

import java.io.Serializable;

public class SeriesCategory implements Serializable {
    private int num;
    private String name;
    private String stream_type;
    private int series_id;
    private String cover;
    private String plot;
    private String cast;
    private String director;
    private String genre;
    private String releaseDate;
    private String rating;
    private int category_id;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getSeries_id() { return series_id; }

    public void setSeries_id(int series_id) { this.series_id = series_id; }

    public String getCover() { return cover; }

    public void setCover(String cover) { this.cover = cover; }

    public SeriesCategory(String name, int series_id, String cover) {
        this.name = name;
        this.series_id = series_id;
        this.cover = cover;
    }
}
