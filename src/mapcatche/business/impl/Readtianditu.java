package mapcatche.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import mapcatche.business.dbdao.Dbtiandtu;
import mapcatche.business.intf.Readmap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Readtianditu implements Readmap {

    @Override
    public byte[] maptile(Map<String, Object> params) {
        // TODO Auto-generated method stub
        Dbtiandtu dbread = new Dbtiandtu();
        return dbread.readTile(params);

    }
    
    public  byte[] downtileimg(Map<String, Object> params) {
        // TODO Auto-generated method stub
        Dbtiandtu dbread = new Dbtiandtu();
        return dbread.readTileImg(params);

    }
    
    public String dbconect(Map<String, Object> params){
    
     Dbtiandtu dbread = new Dbtiandtu();
       Map<String,Object>  callback_obj=new HashMap<String, Object>();
       callback_obj.put("success", dbread.readdbconect(params));
        return JSONObject.fromObject(callback_obj).toString();
    }

    @Override
    public String maptree(Map<String, Object> parmas) {
         Dbtiandtu dbread = new Dbtiandtu();
      //  return dbread.readmapTree
         ArrayList maptreelist=dbread.readmapTree(parmas);
         
         return JSONArray.fromObject(maptreelist).toString();
    }

    @Override
    public String mapower(int mapid) {
        Dbtiandtu dbread = new Dbtiandtu();
        return dbread.readmapOwer(mapid);
    }

    @Override
    public String maptask(Map<String, Object> parmas) {
           Dbtiandtu dbread = new Dbtiandtu();
            Map<String,Object> taskjson=dbread.readmaptask(parmas);
            return JSONObject.fromObject(taskjson).toString();
            
    }
    
    public String downtask (Map<String, Object> parmas) {
           Dbtiandtu dbread = new Dbtiandtu();
            Map<String,Object> taskjson=dbread.readdowntask(parmas);
            return JSONObject.fromObject(taskjson).toString();
            
    }
}
