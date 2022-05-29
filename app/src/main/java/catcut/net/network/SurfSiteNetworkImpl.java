package catcut.net.network;

import android.util.Log;
import catcut.net.network.entity.SurfSite;
import catcut.net.network.entity.SurfSiteListItem;
import catcut.net.network.entity.Token;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurfSiteNetworkImpl extends AuthorizedNetworkImpl implements SurfSiteNetwork {

    final String API_TYPE = "adssite.php";

    public SurfSiteNetworkImpl(Token token) {
        super(token);
    }

    public boolean add(SurfSite surfSite) {

        Map<String, String> additionalParams = new HashMap<>();

        additionalParams.put("site_url", surfSite.site_url);
        additionalParams.put("site_type", String.valueOf(surfSite.site_type));
        additionalParams.put("lang_ad", String.valueOf(surfSite.lang_ad));
        additionalParams.put("site_rate", String.valueOf(surfSite.site_rate));
        additionalParams.put("site_type_budget", String.valueOf(surfSite.site_type_budget));
        additionalParams.put("site_val_budget", String.valueOf(surfSite.site_val_budget));
        additionalParams.put("site_off", surfSite.site_off ? "0" : "1");

        additionalParams.put("geo_type", String.valueOf(surfSite.geo_type));
        JSONArray geoCountries = new JSONArray();
        if (surfSite.geo_country != null)
            for (Short country :
                    surfSite.geo_country) {
                geoCountries.put(country);
            }
        additionalParams.put("geo_country", geoCountries.toString());


        JSONObject data = getJSON(API_TYPE,
                createParams("addedit", additionalParams)
        );

        if (data == null) {
            throw new RuntimeException("AddSurfSite of surf site error NULL");
        }

        try {
            return data.getString("SUCCESS") != null;
        } catch (JSONException e) {
            Log.v("NetworkImpl", "AddSurfSite of surf site error");
            throw new RuntimeException("AddSurfSite of surf site error");
        }
    }


    public boolean edit(SurfSite surfSite) {

        Map<String, String> additionalParams = new HashMap<>();

        additionalParams.put("site_url", surfSite.site_url);

        additionalParams.put("site_id", surfSite.site_id);
        additionalParams.put("site_type", String.valueOf(surfSite.site_type));
        additionalParams.put("lang_ad", String.valueOf(surfSite.lang_ad));
        additionalParams.put("site_rate", String.valueOf(surfSite.site_rate));
        additionalParams.put("site_type_budget", String.valueOf(surfSite.site_type_budget));
        additionalParams.put("site_val_budget", String.valueOf(surfSite.site_val_budget));
        additionalParams.put("site_off", surfSite.site_off ? "0" : "1");

        additionalParams.put("geo_type", String.valueOf(surfSite.geo_type));
        JSONArray geoCountries = new JSONArray();

        if (surfSite.geo_country != null)
            for (Short country :
                    surfSite.geo_country) {
                geoCountries.put(country);
            }
        additionalParams.put("geo_country", geoCountries.toString());

        JSONObject data = getJSON(API_TYPE,
                createParams("addedit", additionalParams)
        );

        if (data == null) {
            throw new RuntimeException("AddSurfSite of surf site error NULL");
        }

        try {
            return data.getString("SUCCESS") != null;
        } catch (JSONException e) {
            Log.v("NetworkImpl", "AddSurfSite of surf site error");
            throw new RuntimeException("AddSurfSite of surf site error");
        }
    }


    public List<SurfSiteListItem> getList(int page) {

        List<SurfSiteListItem> ret = new ArrayList<>();

        JSONArray items = getJSONArray(API_TYPE,
                createParams("list"));

        if (items != null) {

            for (int i = 0; i < items.length(); i++) {
                JSONObject item;
                try {
                    item = (JSONObject) items.get(i);

                    SurfSiteListItem surfSiteListItem = new SurfSiteListItem();

                    surfSiteListItem.site_id = item.getString("site_id");
                    surfSiteListItem.site_url = item.getString("site_url");
                    surfSiteListItem.site_type = item.getString("site_type");
                    surfSiteListItem.lang_ad = item.getString("lang_ad");
                    surfSiteListItem.site_valid = item.getString("site_valid");
                    surfSiteListItem.site_adminnote = item.getString("site_adminnote");
                    surfSiteListItem.site_full_price = item.getString("site_full_price");
                    surfSiteListItem.site_off = item.getString("site_off");
                    surfSiteListItem.site_type_budget = item.getString("site_type_budget");
                    try {
                        surfSiteListItem.site_audience = Short.valueOf(item.getString("site_audience"));
                        surfSiteListItem.site_rate = Short.valueOf(item.getString("site_rate"));
                        surfSiteListItem.adssite_start_price = Double.valueOf(item.getString("adssite_start_price"));
                        surfSiteListItem.site_val_budget = Double.valueOf(item.getString("site_val_budget"));
                        surfSiteListItem.site_shows = Integer.valueOf(item.getString("site_shows"));
                        surfSiteListItem.site_clickback = Integer.valueOf(item.getString("site_clickback"));
                        surfSiteListItem.site_r_budget = Double.valueOf(item.getString("site_r_budget"));
                    } catch (NumberFormatException e) {
                        surfSiteListItem.site_shows = -1;
                        surfSiteListItem.site_clickback = -1;
                        surfSiteListItem.site_r_budget = -1.0;
                        surfSiteListItem.site_audience = -1;
                        surfSiteListItem.adssite_start_price = -1.0;
                        surfSiteListItem.site_val_budget = -1.0;
                        surfSiteListItem.site_rate = -1;
                    }
                    ret.add(surfSiteListItem);
                } catch (JSONException e) {
                    throw new RuntimeException("Get List of surf site error");
                }
            }
        } else {
            throw new RuntimeException("Get List of surf site error");
        }

        return ret;
    }


    public Double getStartPrice() {
        Double ret = 0.0;

        JSONObject data = getJSON(API_TYPE,
                createParams("adssite_start_price"));

        if (data == null) {
            throw new RuntimeException("Get start price error");
        }

        try {
            ret = Double.valueOf(data.getString("adssite_start_price"));
        } catch (JSONException e) {
            Log.v("NetworkImpl", "Error for Start Price");
            throw new RuntimeException("Get start price error");
        } catch (NumberFormatException e) {
            Log.v("NetworkImpl", "Error wrong start prise for adsite");
        }

        return ret;
    }


    public Short updateAudience(short audience) {
        Short ret;

        Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("site_rate", String.valueOf(audience));

        JSONObject data = getJSON(API_TYPE,
                createParams("site_audience", additionalParams)
        );

        if (data == null) {
            throw new RuntimeException("Update audience error");
        }

        try {
            ret = Short.valueOf(data.getString("site_audience"));
        } catch (JSONException e) {
            Log.v("NetworkImpl", "Error for update audience");
            throw new RuntimeException("Update audience error");
        } catch (NumberFormatException e) {
            ret = -1;
            Log.v("NetworkImpl", "Update audience error");
        }

        return ret;
    }
}
