package catcut.net.network.params;

import java.util.HashMap;
import java.util.Map;

public class NetworkParams {


    public enum SERVICE_PARAM {

        APP("app"),
        KEY("key"),
        HASH("hash"),
        MAKE("make");

        private final String text;

        SERVICE_PARAM(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }


    private Map<String, String> params = new HashMap<>();

    public Map<String, String> get() {
        return params;
    }

    public void set(Map<String, String> params) {
        this.params = params;
    }

}
