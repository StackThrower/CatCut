package catcut.net.network.entity;

import java.io.Serializable;

public class SurfSite implements Serializable {

    public String site_url;
    public String site_id;
    public Short site_type;
    public Short lang_ad;
    public Short site_rate;
    public Short site_type_budget;
    public Double site_val_budget;
    public Boolean site_off;

    public Short geo_type;
    public Short[] geo_country;
    public Short[] geo_region_is;
    public Short[][] geo_region;

    public SurfSite() {

    }

    public SurfSite(String site_url,
                    String site_id,
                    Short site_type,
                    Short lang_ad,
                    Short site_rate,
                    Short site_type_budget,
                    Double site_val_budget,
                    Boolean site_off,
                    Short geo_type,
                    Short[] geo_country,
                    Short[] geo_region_is,
                    Short[][] geo_region
    ) {
        this.site_url = site_url;
        this.site_id = site_id;
        this.site_type = site_type;
        this.lang_ad = lang_ad;
        this.site_rate = site_rate;
        this.site_type_budget = site_type_budget;
        this.site_val_budget = site_val_budget;
        this.site_off = site_off;
        this.geo_type = geo_type;
        this.geo_country = geo_country;
        this.geo_region_is = geo_region_is;
        this.geo_region = geo_region;
    }
}
