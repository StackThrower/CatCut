package catcut.net.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import catcut.net.processing.ShortLinkDetailProcessing;
import catcut.net.helpers.NetworkExceptionUI;

import java.net.URL;

public class ShortLinkDetailTask extends GeneralTask {


    public enum CurrentTask {
        LoadQR
    }

    private CurrentTask mode;

    protected ShortLinkDetailProcessing processingCallback;


    Bitmap mIcon_val = null;
    String qrUrl;


    public ShortLinkDetailTask(CurrentTask mode, String qrUrl) {
        this.mode = mode;
        this.qrUrl = qrUrl;
        showProgress(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            switch (mode) {
                case LoadQR:

                    URL newurl = new URL(qrUrl);
                    mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                    break;
            }

        } catch (Exception e) {
            NetworkExceptionUI.showMessageInternalError(activity);
        }

        return true;
    }

    private void processData() {

        switch (mode) {
            case LoadQR:
                if (processingCallback != null)
                    processingCallback.onProcessQRResponse(mIcon_val);
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
        showProgress(false);
    }


    public void setProcessing(ShortLinkDetailProcessing processingCallback) {
        this.processingCallback = processingCallback;
    }


}

