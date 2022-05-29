package catcut.net.network.params;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UnauthorizedNetworkParamsBuilder implements NetworkParamsBuilder {

    static final String APP_ID = "2498";

    NetworkParams networkParams = new NetworkParams();

    String key;

    public UnauthorizedNetworkParamsBuilder() {
        networkParams.get().put("app", APP_ID);
    }

    public void buildKey() {
        key = getKey();
        networkParams.get().put("key", key);
    }

    public void buildHash() {

        String appCode = "";
        try {
            final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            appCode = mFirebaseRemoteConfig.getString("app_code");

        } catch (Exception e) {
        }

        networkParams.get().put("hash", getHash((key + appCode).getBytes()));
    }


    public void buildToken() {

    }

    public void buildParams(Map<String, String> params) {

        if (params != null &&
                params.size() > 0 &&
                params instanceof HashMap) {

            Map<String, String> fields = new HashMap<>(params);


            if (fields.containsKey(null))
                fields.remove(null);

            Iterator it = fields.keySet().iterator();
            while (it.hasNext()) {

                String key = (String) it.next();
                for (NetworkParams.SERVICE_PARAM param :
                        NetworkParams.SERVICE_PARAM.values()) {
                    String serviceFieldName = param.toString();

                    if (serviceFieldName.equalsIgnoreCase(key)) {
                        it.remove();
                    }
                }
            }
            this.networkParams.get().putAll(fields);
        }
    }

    public void buildOperationType(String operation) {
        networkParams.get().put("make", operation);
    }


    public Map<String, String> getParams() {
        return networkParams.get();
    }


    String getHash(byte[] data) {
        String result;

        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            sha1.update(data, 0, data.length);
            byte[] hashBytes = sha1.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < hashBytes.length; i++) {
                sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            result = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA1 issue");
        }

        return result;
    }


    private String getKey() {

        StringBuilder sb = new StringBuilder();
        String time = String.valueOf(System.currentTimeMillis());

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 issue");
        }

        if (md != null) {
            byte[] hashInBytes = md.digest(time.getBytes());

            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
        } else {
            throw new RuntimeException("MD5 issue");
        }

        return sb.toString();
    }
}
