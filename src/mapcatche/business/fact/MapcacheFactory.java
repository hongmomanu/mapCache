package mapcatche.business.fact;

import java.util.Map;

import mapcatche.business.exception.ImpmapException;
import mapcatche.business.impl.Impgoogle;
import mapcatche.business.impl.Imptianditu;
import mapcatche.business.impl.ReadArcgisBundle;
import mapcatche.business.impl.Readtianditu;

public class MapcacheFactory {

    public static void Impmapfactory(Map<String, Object> params) throws ImpmapException {
        if (params.get("mapowner").toString().equalsIgnoreCase("tianditu")) {
            (new Imptianditu()).maptodb(params);
        } else if (params.get("mapowner").toString().equalsIgnoreCase("google")) {
        } else if (params.get("mapowner").toString().equalsIgnoreCase("baidu")) {
        } else {
            throw new ImpmapException("Bad request!");
        }
    }

    public static String Impmapresource(Map<String, Object> params) throws ImpmapException {

        return (new Imptianditu()).mapresourcetodb(params);

    }
    
    public static String deltask(int taskid)throws ImpmapException {
        return (new Imptianditu()).deltask(taskid);
    
    }
    public static String canceltask(int taskid)throws ImpmapException {
        return (new Imptianditu()).canceltask(taskid);
    
    }
    
    public static String mapconfig(Map<String, Object> params ) throws ImpmapException {
    
         return (new Imptianditu()).mapconfig(params);
        
    }
    public static String refinishtask(int taskid) throws ImpmapException {

        return (new Imptianditu()).refinishtask(taskid);

    }

    public static String Imptilefactory(Map<String, Object> params) throws ImpmapException {
        if (params.get("mapowner").toString().equalsIgnoreCase("tianditu")) {
            return (new Imptianditu()).tiletodb(params);
        } else if (params.get("mapowner").toString().equalsIgnoreCase("google")) {
            return (new Impgoogle()).tiletodb(params);
        } else if (params.get("mapowner").toString().equalsIgnoreCase("baidu")) {
            return null;
        } else {
            return null;
        }
    }
    public static String downtilefactory(Map<String, Object> params) throws ImpmapException {
       if (params.get("mapowner")==null) {
             return (new Imptianditu()).downtiles(params);
        } 
       else  if (params.get("mapowner").toString().equalsIgnoreCase("tianditu")) {
            return (new Imptianditu()).downtiles(params);
        } else if (params.get("mapowner").toString().equalsIgnoreCase("google")) {
              return (new Impgoogle()).downtiles(params);
        } else if (params.get("mapowner").toString().equalsIgnoreCase("baidu")) {
            return null;
        } else {
            return null;
        }
    }
public static String Readdbconenct(Map<String,Object>params)throws ImpmapException{
         return (new Readtianditu()).dbconect(params);

}
    
public static byte[]DownTileimg(Map<String,Object>params)throws ImpmapException{
     return (new Readtianditu()).downtileimg(params);
}
    public static byte[] Readmapfactory(Map<String, Object> params) throws ImpmapException {

        String mapower = params.get("mapower").toString();
           
        if (mapower.equalsIgnoreCase("tianditu")) {
            return (new Readtianditu()).maptile(params);
        } else if (mapower.equalsIgnoreCase("google")) {
               return (new Readtianditu()).maptile(params);
        } else if (mapower.equalsIgnoreCase("baidu")) {
            return null;
        }else if(mapower.equalsIgnoreCase("arcgisbundle")){
            return (new ReadArcgisBundle()).maptile(params);
        
        } 
        else {
            throw new ImpmapException("Bad request!");
        }
    }

    public static String ReadmapConfig(String maplabel){
    return (new ReadArcgisBundle()).getcacheconfig(maplabel);
    }
    
    public static String Readmaptree(Map<String, Object> params) throws ImpmapException {
        return (new Readtianditu()).maptree(params);

    }

    public static String Readmaptask(Map<String, Object> params) throws ImpmapException {
        return (new Readtianditu()).maptask(params);

    }
    
    public static String Readdowntask(Map<String, Object> params) throws ImpmapException {
        return (new Readtianditu()).downtask(params);

    }
}
