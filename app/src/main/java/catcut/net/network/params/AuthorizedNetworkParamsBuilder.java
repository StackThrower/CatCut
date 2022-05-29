package catcut.net.network.params;

import catcut.net.network.entity.Token;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class AuthorizedNetworkParamsBuilder extends UnauthorizedNetworkParamsBuilder {

    private Token token;

    private AuthorizedNetworkParamsBuilder() {
    }

    public AuthorizedNetworkParamsBuilder(Token token) {

        this.token = token;
    }

    @Override
    public void buildHash() {

        String appCode = "";

        try {
            final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            appCode = mFirebaseRemoteConfig.getString("app_code");
        } catch (Exception e) {
        }

        networkParams.get().put("hash", getHash((key + token.token + appCode).getBytes()));
    }

    @Override
    public void buildToken() {
        networkParams.get().put("token_id", token.token_id);
    }
}
