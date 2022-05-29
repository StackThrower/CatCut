package catcut.net.network;

import catcut.net.network.entity.Token;
import catcut.net.network.params.AuthorizedNetworkParamsBuilder;

import java.util.Map;

public class AuthorizedNetworkImpl extends NetworkImpl {

    Token token;


    protected AuthorizedNetworkImpl(Token token) {
        this.token = token;
    }

    protected Map<String, String> createParams(String type,
                                               Map<String, String> params) {
        networkParamsBuilder = new AuthorizedNetworkParamsBuilder(token);
        return super.createParams(type, params);
    }
}
