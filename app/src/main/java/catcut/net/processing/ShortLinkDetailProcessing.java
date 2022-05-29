package catcut.net.processing;

import android.graphics.Bitmap;
import catcut.net.network.entity.SurfSite;
import catcut.net.network.entity.SurfSiteListItem;
import catcut.net.tasks.SurfSiteDetailTask;

public interface ShortLinkDetailProcessing {


    void onProcessQRResponse(Bitmap bitmap);



}
