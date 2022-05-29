package catcut.net.tasks;

import catcut.net.processing.ShortLinkProcessing;
import catcut.net.helpers.NetworkExceptionUI;
import catcut.net.helpers.SharedPreferencesHelper;
import catcut.net.network.ShortLinkNetwork;
import catcut.net.network.ShortLinkNetworkImpl;

public class ShortLinkTask extends GeneralTask {

    public enum CurrentTask {
        EncodeShortLink,
    }


    private CurrentTask mode;

    private String longUrl;
    private String shortUrl;

    protected ShortLinkProcessing shortLinkProcessing;

    public ShortLinkTask(CurrentTask mode, String longUrl) {
        this.mode = mode;
        this.longUrl = longUrl;
        showProgress(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            ShortLinkNetwork networkImpl = new ShortLinkNetworkImpl(
                    SharedPreferencesHelper.getStoredToken(activity)
            );

            switch (mode) {
                case EncodeShortLink:
                    shortUrl = networkImpl.encodeUrl(longUrl);
                    break;
            }

        } catch (Exception e) {
            NetworkExceptionUI.showMessageNoInternetConnection(activity, e.getMessage());
        }

        return true;
    }

    private void processData() {


        switch (mode) {
            case EncodeShortLink:

                if (shortLinkProcessing != null)
                    shortLinkProcessing.onProcessShortLinkResponse(shortUrl);
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


    public void setProcessing(ShortLinkProcessing shortLinkProcessing) {
        this.shortLinkProcessing = shortLinkProcessing;
    }
}
