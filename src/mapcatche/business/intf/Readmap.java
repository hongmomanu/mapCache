package mapcatche.business.intf;

import java.util.Map;

public interface Readmap {

    byte[] maptile(Map<String, Object> parmas);
    String maptree(Map<String, Object> parmas);
    String maptask(Map<String, Object> parmas);
    String mapower(int mapid);
}
