package catcut.net.network;

import catcut.net.network.params.NetworkParamsBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NetworkImpl {

    private static final String CATCUT_API_URL = "https://catcut.net/api/%s";
    NetworkParamsBuilder networkParamsBuilder;


    protected Map<String, String> createParams(String type,
                                               Map<String, String> params) {
        if (networkParamsBuilder != null) {
            networkParamsBuilder.buildKey();
            networkParamsBuilder.buildHash();
            networkParamsBuilder.buildToken();
            networkParamsBuilder.buildOperationType(type);
            networkParamsBuilder.buildParams(params);

            return networkParamsBuilder.getParams();
        } else {
            return new HashMap<>();
        }
    }

    protected Map<String, String> createParams(String type) {
        return createParams(type, new HashMap<String, String>());
    }


    protected JSONObject getJSON(String uri, Map<String, String> params) {

        JSONObject data;
        try {
            data = new JSONObject(performRequest(uri, params));
        } catch (JSONException e) {
            throw new RuntimeException("JSONObject issue");
        }

        return data;
    }

    protected JSONArray getJSONArray(String uri, Map<String, String> params) {
        try {

            JSONArray data = new JSONArray(performRequest(uri, params));

            return data;
        } catch (JSONException e) {
            throw new RuntimeException("JSONArray issue error:" + e.getMessage() + " uri:"+ uri + " params:" + params.toString());
        }
    }

    // TODO move JSON parsers here
    protected void getResponse() {

    }


    private synchronized String performRequest(String uri, Map<String, String> params) {

        try {
            URL url = new URL(String.format(CATCUT_API_URL, uri));
            final HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
                sb.append("&");
            }

            byte[] postData = sb.toString().getBytes();
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(postData);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuilder response = new StringBuilder(1024);
            String tmp;
            while ((tmp = reader.readLine()) != null)
                response.append(tmp).append("\n");
            reader.close();


            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("NetworkImpl issue");
            }

            return response.toString();

        } catch (Exception e) {
            throw new RuntimeException("NetworkImpl issue:" + e.getMessage());
        }
    }


}
