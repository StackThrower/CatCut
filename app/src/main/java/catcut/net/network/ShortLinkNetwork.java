package catcut.net.network;

import catcut.net.network.entity.LinkStat;

import java.util.List;

public interface ShortLinkNetwork {


    String encodeUrl(String url);

    List<LinkStat> getList(int page);

    boolean hideUrls(String urls);

    boolean hideAdForUrl(String ids, boolean showAd);


}
