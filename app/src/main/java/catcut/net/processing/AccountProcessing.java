package catcut.net.processing;

import catcut.net.network.entity.AccountInfo;
import catcut.net.tasks.AccountTask;

public interface AccountProcessing {


    void onProcessAccountInfoResponse(AccountInfo accountInfo);
}
