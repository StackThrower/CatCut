package catcut.net.tasks;

import catcut.net.processing.AccountProcessing;
import catcut.net.helpers.NetworkExceptionUI;
import catcut.net.helpers.SharedPreferencesHelper;
import catcut.net.network.AccountNetwork;
import catcut.net.network.entity.AccountInfo;
import catcut.net.network.params.AccountNetworkImpl;

public class AccountTask extends GeneralTask {


    AccountInfo accountInfo;
    AccountProcessing accountProcessing;

    public enum CurrentTask {
        Info
    }

    private CurrentTask mode;


    public AccountTask(CurrentTask mode) {
        this.mode = mode;
        showProgress(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {

            AccountNetwork accountNetwork = new AccountNetworkImpl(
                    SharedPreferencesHelper.getStoredToken(activity)
            );

            switch (mode) {
                case Info:

                    accountInfo = accountNetwork.getInfo();

                    break;
            }
        } catch (Exception e) {
            NetworkExceptionUI.showMessageNoInternetConnection(activity, e.getMessage());
        }


        return true;
    }


    private void processData() {


        switch (mode) {
            case Info:

                if (accountProcessing != null)
                    accountProcessing.onProcessAccountInfoResponse(accountInfo);
                break;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        super.onPostExecute(success);
        processData();
        showProgress(false);
    }

    @Override
    protected void onCancelled(final Boolean success) {
        super.onCancelled(success);
        processData();
        showProgress(false);
    }

    public void setProcessing(AccountProcessing accountProcessing) {
        this.accountProcessing = accountProcessing;
    }
}
