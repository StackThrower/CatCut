package catcut.net.network.params;

import android.util.Log;
import catcut.net.network.AccountNetwork;
import catcut.net.network.AuthorizedNetworkImpl;
import catcut.net.network.entity.AccountInfo;
import catcut.net.network.entity.Token;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountNetworkImpl extends AuthorizedNetworkImpl implements AccountNetwork {

    public AccountNetworkImpl(Token token) {
        super(token);
    }


    public AccountInfo getInfo() {
        AccountInfo ret = new AccountInfo();

        Map<String, String> params = new HashMap<>();

        params.put("make", "info");

        JSONObject data = getJSON("account.php", createParams("info"));

        if (data != null)
            try {
                ret.adsbalance = data.getString("adsbalance");
                ret.workbalance = data.getString("workbalance");
                ret.invurl = data.getString("invurl");
            } catch (JSONException e) {
                Log.v("NetworkImpl", "Error for AccountInfo");
            }

        return ret;
    }


}
