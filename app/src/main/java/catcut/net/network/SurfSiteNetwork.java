package catcut.net.network;

import catcut.net.network.entity.SurfSite;
import catcut.net.network.entity.SurfSiteListItem;

import java.util.List;

public interface SurfSiteNetwork {

    boolean add(SurfSite surfSite);

    boolean edit(SurfSite surfSite);

    List<SurfSiteListItem> getList(int page);

    Double getStartPrice();

    Short updateAudience(short audience);
}
