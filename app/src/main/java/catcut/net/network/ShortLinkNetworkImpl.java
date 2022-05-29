package catcut.net.network;

import android.util.Log;
import catcut.net.network.entity.LinkStat;
import catcut.net.network.entity.Token;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShortLinkNetworkImpl extends AuthorizedNetworkImpl implements ShortLinkNetwork {

    final String API_TYPE = "links.php";


    public ShortLinkNetworkImpl(Token token) {
        super(token);
    }

    public String encodeUrl(String url) {

        Map<String, String> params = new HashMap<>();

        try {
            params.put("longurl", URLEncoder.encode(url, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encode url issue");
        }
        params.put("advsurfing", "1");
        params.put("comment", "");

        JSONObject data = getJSON(API_TYPE, createParams("shorturl", params));

        if (data == null) {
            throw new RuntimeException("Encode url issue");
        }

        try {
            return "http://ccl.su/" + data.getString("url");
        } catch (JSONException e) {
            Log.v("NetworkImpl", "empty token");
            throw new RuntimeException("Encode url issue");
        }
    }

    public List<LinkStat> getList(int page) {

        List<LinkStat> ret = new ArrayList<>();

        JSONObject data = getJSON(API_TYPE,
                createParams("list"));

        if (data != null) {

            JSONArray links;
            try {
                links = data.getJSONArray("links");
            } catch (JSONException e) {
                throw new RuntimeException("Parsing of link list error");
            }

            if (links == null) {
                throw new RuntimeException("Encode url issue");
            }

            for (int i = 0; i < links.length(); i++) {
                JSONObject link;
                try {
                    link = (JSONObject) links.get(i);

                    LinkStat linkStat = new LinkStat();
                    linkStat.id = link.getString("id");
                    linkStat.shortlink = link.getString("shorturl");
                    linkStat.createtime = link.getString("createtime");
                    linkStat.count = link.getString("count");
                    linkStat.longurl = link.getString("longurl");
                    linkStat.advsurfing = link.getString("advsurfing");
                    linkStat.money = link.getString("money");

                    ret.add(linkStat);
                } catch (JSONException e) {
                    throw new RuntimeException("Short link list error");
                }
            }
        }

        return ret;
    }

    public boolean hideUrls(String urls) {

        boolean ret = false;

        Map<String, String> params = new HashMap<>();

        params.put("ids", urls);

        JSONObject data = getJSON(API_TYPE, createParams("hide", params));

        if (data != null)
            try {
                ret = data.getString("SUCCESS") != null;
            } catch (JSONException e) {
                Log.v("NetworkImpl", "hide link error");
                throw new RuntimeException("Short link list error");
            }

        return ret;
    }

    public boolean hideAdForUrl(String ids, boolean showAd) {

        boolean ret = false;

        Map<String, String> params = new HashMap<>();

        params.put("ids", ids);
        params.put("value", showAd ? "1" : "0");

        JSONObject data = getJSON(API_TYPE, createParams("advsurfing", params));

        if (data != null)
            try {
                ret = data.getString("SUCCESS") != null;
            } catch (JSONException e) {
                Log.v("NetworkImpl", "switch ad for link error");
                throw new RuntimeException("Short link list error");
            }

        return ret;
    }


}
