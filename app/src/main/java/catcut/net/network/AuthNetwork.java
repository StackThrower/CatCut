package catcut.net.network;

import catcut.net.network.entity.Token;

public interface AuthNetwork {

    boolean getPin(String email);

    boolean createNewAccount(String email);

    Token getToken(String email, String code);
}
