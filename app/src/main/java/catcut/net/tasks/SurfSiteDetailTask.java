package catcut.net.tasks;

import catcut.net.processing.SurfSiteDetailProcessing;
import catcut.net.helpers.NetworkExceptionUI;
import catcut.net.helpers.SharedPreferencesHelper;
import catcut.net.network.SurfSiteNetwork;
import catcut.net.network.SurfSiteNetworkImpl;
import catcut.net.network.entity.SurfSite;
import catcut.net.network.entity.SurfSiteListItem;

public class SurfSiteDetailTask extends GeneralTask {


    public enum CurrentTask {
        AddSurfSite,
        EditSurfSite,
        GetSurfSite,

        GetStartPrice,
        ChangeAudience
    }

    private SurfSite surfSite = new SurfSite();
    private SurfSiteListItem surfSiteListItem = new SurfSiteListItem();

    private CurrentTask mode;

    protected SurfSiteDetailProcessing processingCallback;

    public SurfSiteDetailTask(CurrentTask mode) {
        this.mode = mode;
        showProgress(true);
    }

    public SurfSiteDetailTask(CurrentTask mode, SurfSite surfSite) {
        this.mode = mode;
        this.surfSite = surfSite;

        showProgress(true);
    }

    public SurfSiteDetailTask(CurrentTask mode, SurfSiteListItem surfSiteListItem) {
        this.mode = mode;
        this.surfSiteListItem = surfSiteListItem;

        showProgress(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            SurfSiteNetwork networkImpl = new SurfSiteNetworkImpl(
                    SharedPreferencesHelper.getStoredToken(activity)
            );

            switch (mode) {
                case AddSurfSite:
                    networkImpl.add(surfSite);
                    break;
                case EditSurfSite:
                    networkImpl.edit(surfSite);
                    break;
                case GetStartPrice:
                    surfSiteListItem.adssite_start_price =
                            networkImpl.getStartPrice();
                    break;

                case ChangeAudience:
                    surfSiteListItem.site_audience =
                            networkImpl.updateAudience(surfSiteListItem.site_rate);
                    break;
            }

        } catch (Exception e) {
            NetworkExceptionUI.showMessageNoInternetConnection(activity, e.getMessage());
        }

        return true;
    }

    private void processData() {


        switch (mode) {
            case AddSurfSite:
            case EditSurfSite:

                if (processingCallback != null)
                    processingCallback.onProcessSurfSiteResponse(surfSite, mode);
                break;

            case GetStartPrice:
            case ChangeAudience:

                if (processingCallback != null)
                    processingCallback.onProcessSurfSiteItemListResponse(surfSiteListItem, mode);
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


    public void setProcessing(SurfSiteDetailProcessing processingCallback) {
        this.processingCallback = processingCallback;
    }


}

