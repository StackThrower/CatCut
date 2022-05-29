package catcut.net.network;

import android.app.Activity;
import android.os.AsyncTask;
import catcut.net.helpers.NetworkExceptionUI;
import catcut.net.helpers.SharedPreferencesHelper;

public class NetworkAsync extends AsyncTask<Void, Void, Boolean> {

    Activity activity;
    String method = "";

    String hideUrls;
    String ids;
    boolean showAd;

    public NetworkAsync(Activity activity) {
        this.activity = activity;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            ShortLinkNetwork networkImpl = new ShortLinkNetworkImpl(SharedPreferencesHelper.getStoredToken(activity));

            switch (method) {
                case "hideUrls":
                    networkImpl.hideUrls(hideUrls);
                    break;
                case "hideAdForUrl":
                    networkImpl.hideAdForUrl(ids, showAd);
                    break;
            }
        } catch (Exception e) {
            NetworkExceptionUI.showMessageNoInternetConnection(activity, e.getMessage());
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

    }

    @Override
    protected void onCancelled(final Boolean success) {

    }

    public void hideUrls(String urls) {
        method = "hideUrls";
        hideUrls = urls;
    }


    public void hideAdForUrl(String ids, boolean show) {
        method = "hideAdForUrl";
        this.ids = ids;
        showAd = show;
    }

}
