package catcut.net.processing;

import catcut.net.network.entity.SurfSite;
import catcut.net.network.entity.SurfSiteListItem;
import catcut.net.tasks.SurfSiteDetailTask;

public interface SurfSiteDetailProcessing {


    void onProcessSurfSiteResponse(SurfSite surfSite, SurfSiteDetailTask.CurrentTask mode);

    void onProcessSurfSiteItemListResponse(SurfSiteListItem surfSiteListItem, SurfSiteDetailTask.CurrentTask mode);

}
