package catcut.net.helpers;

import android.content.ContextWrapper;
import android.content.SharedPreferences;
import catcut.net.network.entity.Token;

public class SharedPreferencesHelper {


    public static Token getStoredToken(ContextWrapper wrapper) {
        Token ret = new Token();

        if (wrapper != null) {
            SharedPreferences mPrefs = wrapper.getSharedPreferences("label", 0);
            ret.token = mPrefs.getString("token", "");
            ret.token_id = mPrefs.getString("token_id", "");
        } else {
            throw new RuntimeException("Token issue");
        }

        return ret;
    }

}
