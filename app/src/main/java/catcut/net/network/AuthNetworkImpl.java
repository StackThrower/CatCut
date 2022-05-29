package catcut.net.network;

import android.util.Log;
import catcut.net.BuildConfig;
import catcut.net.network.entity.Token;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthNetworkImpl extends UnauthorizedNetworkImpl implements AuthNetwork {


    final String API_TYPE = "auth.php";


    public boolean getPin(String email) {
        boolean ret;

        Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("login", email);

        JSONObject data = getJSON(API_TYPE,
                createParams("code", additionalParams));

        if (data == null) {
            throw new RuntimeException("Get pin issue");
        }

        try {
            ret = (data.getString("SUCCESS") != null);
            Log.v("NetworkImpl", ret ? "signed in" : "error");
            if (!ret) {
                Log.v("NetworkImpl", data.getString("ERROR"));
                throw new RuntimeException("Get pin issue");
            }
            return true;

        } catch (JSONException e) {
            throw new RuntimeException("Get pin issue");
        }
    }

    public boolean createNewAccount(String email) {
        boolean ret;
        Map<String, String> additionalParams = new HashMap<>();

        additionalParams.put("ref", "394951");
        additionalParams.put("login", email);

        JSONObject data = getJSON(API_TYPE,
                createParams("new", additionalParams));

        if (data == null) {
            throw new RuntimeException("Create New Account Issue");
        }

        try {
            ret = (data.getString("SUCCESS") != null);
            Log.v("NetworkImpl", ret ?
                    "Account created successfully" :
                    "Error appeared during of create of new account");
            if (!ret) {
                Log.v("NetworkImpl", data.getString("ERROR"));
            }

            return ret;

        } catch (JSONException e) {
            throw new RuntimeException("Create New Account Issue");
        }
    }

    public Token getToken(String email, String code) {
        Token ret = new Token();

        Map<String, String> additionalParams = new HashMap<>();

        String deviceName = android.os.Build.MODEL + "_v" + BuildConfig.VERSION_NAME;

        additionalParams.put("login", email);
        additionalParams.put("code", code);
        additionalParams.put("device_name", deviceName);

        JSONObject data = getJSON(API_TYPE,
                createParams("token", additionalParams));

        if (data == null) {
            throw new RuntimeException("Wrong token Issue");
        }


        try {
                ret.token = data.getString("token");
                ret.token_id = data.getString("token_id");
            } catch (JSONException e) {
                Log.v("NetworkImpl", "empty token");
                throw new RuntimeException("Wrong token Issue");
            }

        return ret;
    }


}
