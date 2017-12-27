package Network;

import Adapters.LiveCategoriesAdapter;
import Adapters.LiveChannelsAdapter;
import Adapters.ReplayAdapter;
import Adapters.ReplayChannelsAdapter;
import Adapters.VodCategoriesAdapter;
import Adapters.VodStreamsAdapter;
import Models.Category;
import Models.Channel;
import Models.Epg;
import Models.Movie;
import Settings.Account;
import android.util.Base64;
import android.util.Log;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import z.xtreamiptv.player.utils.TtmlNode;

public class XtreamCodesApi {
    private Account account = new Account();
    private List<Category> liveCategories;
    private List<Category> vodCategories;

    class C07042 implements ErrorListener {
        C07042() {
        }

        public void onErrorResponse(VolleyError error) {
        }
    }

    class C07064 implements ErrorListener {
        C07064() {
        }

        public void onErrorResponse(VolleyError error) {
        }
    }

    class C07086 implements ErrorListener {
        C07086() {
        }

        public void onErrorResponse(VolleyError error) {
        }
    }

    class C07108 implements ErrorListener {
        C07108() {
        }

        public void onErrorResponse(VolleyError error) {
        }
    }

    public void getLiveCategories(final LiveCategoriesAdapter adapter) {
        VolleySingleton.getInstance().addToRequestQueue(new JsonArrayRequest(0, this.account.getHost() + "player_api.php?username=" + this.account.getUsername() + "&password=" + this.account.getPassword() + "&action=get_live_categories", null, new Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                XtreamCodesApi.this.liveCategories = XtreamCodesApi.this.parseCategories(response);
                adapter.setLiveCategories(XtreamCodesApi.this.liveCategories);
            }
        }, new C07042()));
    }

    public void getVodCategories(final VodCategoriesAdapter adapter) {
        VolleySingleton.getInstance().addToRequestQueue(new JsonArrayRequest(0, this.account.getHost() + "player_api.php?username=" + this.account.getUsername() + "&password=" + this.account.getPassword() + "&action=get_vod_categories", null, new Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                XtreamCodesApi.this.vodCategories = XtreamCodesApi.this.parseCategories(response);
                adapter.setVodCategories(XtreamCodesApi.this.vodCategories);
            }
        }, new C07064()));
    }

    private List<Category> parseCategories(JSONArray data) {
        List<Category> cats = new ArrayList();
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject cat = data.getJSONObject(i);
                cats.add(new Category(cat.getInt("category_id"), cat.getString("category_name"), cat.getInt("parent_id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cats.add(new Category(0, "All", 0));
        return cats;
    }

    public void getCategoryChannels(int id, final LiveChannelsAdapter mAdapter) {
        VolleySingleton.getInstance().addToRequestQueue(new JsonArrayRequest(0, this.account.getHost() + "player_api.php?username=" + this.account.getUsername() + "&password=" + this.account.getPassword() + "&action=get_live_streams&category_id=" + id, null, new Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                mAdapter.setLiveChannels(XtreamCodesApi.this.parseLiveChannels(response));
            }
        }, new C07086()));
    }

    private List<Channel> parseLiveChannels(JSONArray data) {
        List<Channel> channels = new ArrayList();
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject ch = data.getJSONObject(i);
                channels.add(new Channel(ch.getString("name"), ch.getInt("stream_id"), ch.getString("stream_icon"), ch.getString("epg_channel_id"), ch.getInt("tv_archive"), ch.getInt("tv_archive_duration")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return channels;
    }

    public void getCategoryMovies(int id, final VodStreamsAdapter mAdapter) {
        VolleySingleton.getInstance().addToRequestQueue(new JsonArrayRequest(0, this.account.getHost() + "player_api.php?username=" + this.account.getUsername() + "&password=" + this.account.getPassword() + "&action=get_vod_streams&category_id=" + id, null, new Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                mAdapter.setMovies(parseMovies(response));
            }

            private List<Movie> parseMovies(JSONArray response) {
                List<Movie> movies = new ArrayList();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject movie = response.getJSONObject(i);
                        movies.add(new Movie(movie.getString("name"), movie.getInt("stream_id"), movie.getString("stream_icon"), movie.getString("container_extension")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return movies;
            }
        }, new C07108()));
    }

    public void getEpg(int id, final int index, final LiveChannelsAdapter channelAdapter) {
        VolleySingleton.getInstance().addToRequestQueue(new JsonObjectRequest(0, this.account.getHost() + "player_api.php?username=" + this.account.getUsername() + "&password=" + this.account.getPassword() + "&action=get_short_epg&stream_id=" + id + "&limit=1", null, new Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                parseEpg(response);
            }

            private void parseEpg(JSONObject raw_epg) {
                try {
                    JSONObject prog = raw_epg.getJSONArray("epg_listings").getJSONObject(0);
                    channelAdapter.setChannelEpg(new Epg(new String(Base64.decode(prog.getString("title"), 0)), prog.getString(TtmlNode.START), prog.getString(TtmlNode.END), prog.getString("description"), prog.getString("epg_id")), index);
                } catch (JSONException e) {
                    channelAdapter.setChannelEpg(new Epg("No Epg Data...", "", "", "", ""), index);
                }
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("Shadow", "Error getting EPG" + error.toString());
            }
        }));
    }

    public void getReplayChannels(final ReplayChannelsAdapter mAdapter) {
        VolleySingleton.getInstance().addToRequestQueue(new JsonArrayRequest(0, this.account.getHost() + "player_api.php?username=" + this.account.getUsername() + "&password=" + this.account.getPassword() + "&action=get_live_streams", null, new Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                List<Channel> rawChan = XtreamCodesApi.this.parseLiveChannels(response);
                Iterator<Channel> it = rawChan.iterator();
                while (it.hasNext()) {
                    if (((Channel) it.next()).getTv_archive() == 0) {
                        it.remove();
                    }
                }
                Log.i("shadow", rawChan.toString());
                mAdapter.setChannels(rawChan);
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }));
    }

    public void getReplayEpg(final int id, final ReplayAdapter replayAdapter) {
        VolleySingleton.getInstance().addToRequestQueue(new JsonObjectRequest(0, this.account.getHost() + "player_api.php?username=" + this.account.getUsername() + "&password=" + this.account.getPassword() + "&action=get_simple_data_table&stream_id=" + id, null, new Listener<JSONObject>() {
            List<Epg> progs = new ArrayList();

            public void onResponse(JSONObject response) {
                parseEpg(response);
            }

            private void parseEpg(JSONObject raw_epg) {
                try {
                    JSONArray epgs = raw_epg.getJSONArray("epg_listings");
                    for (int i = 0; i < epgs.length(); i++) {
                        try {
                            JSONObject prog = raw_epg.getJSONArray("epg_listings").getJSONObject(i);
                            byte[] title = Base64.decode(prog.getString("title"), 0);
                            if (prog.getInt("has_archive") != 0) {
                                this.progs.add(new Epg(new String(title), prog.getString(TtmlNode.START), prog.getString(TtmlNode.END), prog.getString("description"), prog.getString("epg_id"), prog.getString("start_timestamp"), prog.getString("stop_timestamp")));
                            }
                        } catch (JSONException e) {
                            this.progs.add(new Epg("No Epg Data...", "", "", "", ""));
                        }
                    }
                } catch (JSONException e2) {
                    this.progs.add(new Epg("No Epg Data...", "", "", "", ""));
                }
                replayAdapter.setProgs(this.progs, id);
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("Shadow", "Error getting EPG" + error.toString());
            }
        }));
    }
}
