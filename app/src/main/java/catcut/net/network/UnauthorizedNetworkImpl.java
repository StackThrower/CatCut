package catcut.net.network;

import catcut.net.network.params.UnauthorizedNetworkParamsBuilder;

import java.util.Map;

public class UnauthorizedNetworkImpl extends NetworkImpl {


    protected Map<String, String> createParams(String type,
                                               Map<String, String> params) {
        networkParamsBuilder = new UnauthorizedNetworkParamsBuilder();
        return super.createParams(type, params);
    }

}
