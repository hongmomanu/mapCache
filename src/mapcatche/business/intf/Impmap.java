package mapcatche.business.intf;

import java.util.Map;

public interface Impmap {

    void maptodb(Map<String, Object> params);
    String tiletodb(Map<String,Object>params);
}
