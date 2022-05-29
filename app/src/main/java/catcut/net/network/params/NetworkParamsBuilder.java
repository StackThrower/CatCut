package catcut.net.network.params;

import java.util.Map;

public interface NetworkParamsBuilder {

    void buildKey();

    void buildHash();

    void buildToken();

    void buildParams(Map<String, String> params);

    void buildOperationType(String operation);

    Map<String, String> getParams();


}
