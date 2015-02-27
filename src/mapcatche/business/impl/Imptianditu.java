package mapcatche.business.impl;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.log4j.Logger;

import mapcatche.business.dbdao.Dbtiandtu;
import mapcatche.business.intf.Impmap;
import mapcatche.conmmon.Config;
import mapcatche.conmmon.ErrorFunc;
import mapcatche.implmapfunc.ImplToPg;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Imptianditu implements Impmap {

    private static final Logger log = Logger.getLogger(Imptianditu.class);
    private static int tileSizeWidth = 256;
    private static int tileSizeHeight = 256;
    private static int originLon = -180;
    private static int originLat = -90;
    private static int topTileFromX = -180;
    private static int topTileFromY = 90;
    public static Map<Integer, ArrayList<Thread>> threadMap = new HashMap<Integer, ArrayList<Thread>>();

    @Override
    public String tiletodb(Map<String, Object> params) {
        
        String[] levels=(String[]) params.get("layerlevel");
        
        for(int i=0;i<levels.length;i++){
            
             Map<String, Object> url_params = new HashMap<String, Object>();

                for (int j = 0; j < params.keySet().toArray().length; j++) {
                    String key = params.keySet().toArray()[j].toString();
                    url_params.put(key, params.get(key));
                }
                url_params.put("layerlevel",  levels[i]);

            
            
            //params.put("layerlevel", levels[i]);
             int layerlevelid = layerLevel(url_params);
               url_params.put("layerlevelid", layerlevelid);
            int taskid = makeTask(url_params);
            url_params.put("taskid", taskid);
            
            MakeTileThread m=new MakeTileThread(url_params, taskid);
            Thread t=new Thread(m);
            t.start();
        
        }
        /**
        int layerlevelid = layerLevel(params);
        if (layerlevelid != Dbtiandtu.keydefault) {
            params.put("layerlevelid", layerlevelid);
            int taskid = makeTask(params);
            params.put("taskid", taskid);
            
            MakeTileThread m=new MakeTileThread(params, taskid);
            Thread t=new Thread(m);
            t.start();
            
           // mapCacheControl(params);
        return "{success:true}";
        }else{
        return "{success:false}";
        }**/
        return "{success:true}";
    }
    
    
    
    public String downtiles(Map<String, Object> params) {
        
        String[] levels=(String[]) params.get("layerlevel");
        
        String type=params.get("type")==null?null:params.get("type").toString();
        if(type!=null&&!type.equals("")){
        
            if(type.equals("del")){
            
                int taskid=Integer.parseInt(params.get("taskid").toString());
                 Dbtiandtu tddao = new Dbtiandtu();
                 tddao.deldowntask(taskid);
            
            }
            
        
        }
        else{
          for(int i=0;i<levels.length;i++){
            
             Map<String, Object> url_params = new HashMap<String, Object>();

                for (int j = 0; j < params.keySet().toArray().length; j++) {
                    String key = params.keySet().toArray()[j].toString();
                    url_params.put(key, params.get(key));
                }
                url_params.put("layerlevel",  levels[i]);
                
                DownTileThread m=new DownTileThread(url_params);
                 Thread t=new Thread(m);
                 t.start();

            //   makeDownTiles(url_params);
        
        }
        }
        
        
      
     
        return "{success:true}";
    }
    
    public String mapresourcetodb(Map<String, Object> params){
        
        
        Dbtiandtu tddao = new Dbtiandtu();
        if(tddao.mapresourcetodb(params)>0){
              return "{success:true}";
        }
        else{
            return "{success:false}";
        }
                
        
    }
    
    public String deltask(int taskid){
    Dbtiandtu tddao = new Dbtiandtu();
    if(tddao.deltask(taskid)>0){
      return "{success:true}";
    }else{
        return "{success:false}";
    }
    
    }
    
    public String canceltask(int taskid){
        Dbtiandtu tddao = new Dbtiandtu();
        tddao.canceltask(taskid);
        return "{success:true}";
    
    }
    
    public String mapconfig(Map<String, Object> params){
    
        Map<String,Object> result=new HashMap();
                Config scaleconfig = Config.getConfig("config.properties");
        String type=params.get("type").toString();
        result.put("success",true);
        
        ///log.debug("hello");
        //log.debug(type);
        if(type.equals("read")){
           // log.debug("begin");
        result.put("data", scaleconfig.getmapValues());
        return JSONObject.fromObject(result ).toString();
        }
        else {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String name = entry.getKey();
                if(name.equals("type"))continue;
                String val = entry.getValue().toString();
                scaleconfig.setValue(name, val);
                
            }
           Dbtiandtu.freshobj();
           Config.freshObj();
        return JSONObject.fromObject(result).toString();
        }
                
    
    }
    
    public String refinishtask(int taskid){
        Dbtiandtu tddao = new Dbtiandtu();
        
       // String totalCountSql="select count(*) from "+task
        Imptianditu.threadMap.put(taskid, new ArrayList<Thread>());
        tddao.updateTaskState(TaskState.Begin.getState(), taskid);
        //tddao.getTasktiles(taskid); //运行版
        tddao.makeTaskTiles(taskid); //测试版
        
        //tddao.refinishtask(taskid);
              return "{success:true}";
        
        
    }

    @Override
    public void maptodb(Map<String, Object> params) {
        /**
         * mapname,layername,maxx,minx,maxy,miny,level
		 * *
         */
        int mapid = saveMapinfo(params);
        if (mapid != Dbtiandtu.keydefault) {
            params.put("mapid", mapid);
            int layerid = layerType(params);
            if (layerid != Dbtiandtu.keydefault) {
                params.put("layerid", layerid);
                int layerlevelid = layerLevel(params);
                if (layerlevelid != Dbtiandtu.keydefault) {
                    params.put("layerlevelid", layerlevelid);

                    int taskid = makeTask(params);
                    params.put("taskid", taskid);
                    mapCacheControl(params);


                }
            }
        }


    }

    private int saveMapinfo(Map<String, Object> params) {
        /*
         * mapname,maplabel,mapowner,spatialreference,projection
         * */
        Dbtiandtu tddao = new Dbtiandtu();
        return tddao.MapinfoToDb(params);

    }

    private int layerType(Map<String, Object> params) {
        /*
         * mapid,layername,layerlabel
         * */
        Dbtiandtu tddao = new Dbtiandtu();
        return tddao.layerType(params);

    }

    private int layerLevel(Map<String, Object> params) {
        /*
         * layerid,layerlevel,resolution,scale
         * */
        Dbtiandtu tddao = new Dbtiandtu();

        return tddao.layerLevel(params);
    }

    private int makeTask(Map<String, Object> params) {
        /*
         * layerid,layerlevel,maxx,minx,maxy,miny
         * 
         * */
        Dbtiandtu tddao = new Dbtiandtu();
        return tddao.makeTask(params);

    }

    private ArrayList<Map<String, Double>> filerExtent(ArrayList<Map<String, Double>> cacheExtentObj) {
        ArrayList<Map<String, Double>> extentObj = new ArrayList<Map<String, Double>>();
        for (Map<String, Double> map : cacheExtentObj) {

            if (map == null || map.isEmpty() || map.get("minx").doubleValue() == map.get("maxx").doubleValue()
                    || map.get("maxy").doubleValue() == map.get("miny").doubleValue()) {
                //log.debug(map.get("maxy"));
                continue;

            }
            extentObj.add(map);
        }
        return extentObj;
    }

    private ArrayList<Map<String, Double>> boundsExtent(ArrayList<Map<String, Object>> bounds_array,
            ArrayList<Map<String, Double>> cacheExtentObj, int n) {
        if (n == bounds_array.size()) {
            return filerExtent(cacheExtentObj);
        } else {
            ArrayList<Map<String, Double>> newcacheExtentObj = new ArrayList<Map<String, Double>>();
            for (Map<String, Double> map : cacheExtentObj) {
                if (map == null || map.size() == 0) {
                    continue;
                }
                double begin_minx = map.get("minx");
                double begin_miny = map.get("miny");
                double begin_maxx = map.get("maxx");
                double begin_maxy = map.get("maxy");


                if (begin_minx == begin_maxx || begin_miny == begin_maxy) {
                    //cacheExtentObj.remove(map);
                    //continue;
                }

                newcacheExtentObj.add(map);

                double minx = Double.parseDouble(bounds_array.get(n).get("minx").toString());
                double maxx = Double.parseDouble(bounds_array.get(n).get("maxx").toString());
                double miny = Double.parseDouble(bounds_array.get(n).get("miny").toString());
                double maxy = Double.parseDouble(bounds_array.get(n).get("maxy").toString());




                Map<String, Double> map_left = new HashMap<String, Double>();
                Map<String, Double> map_bottom = new HashMap<String, Double>();
                Map<String, Double> map_right = new HashMap<String, Double>();
                Map<String, Double> map_top = new HashMap<String, Double>();

                if (begin_minx <= minx && begin_maxx >= minx && begin_miny <= maxy && begin_maxy >= miny) {

                    newcacheExtentObj.remove(map);

                    map_left.put("minx", begin_minx);
                    map_left.put("miny", begin_miny);
                    map_left.put("maxy", begin_maxy);
                    map_left.put("maxx", minx);



                    map_right.put("minx", maxx);
                    map_right.put("miny", begin_miny);
                    map_right.put("maxy", begin_maxy);
                    map_right.put("maxx", begin_maxx);








                    if (begin_maxx < maxx) {

                        map_bottom.put("minx", minx);
                        map_bottom.put("miny", begin_miny);
                        map_bottom.put("maxy", miny);
                        map_bottom.put("maxx", begin_maxx);

                        map_top.put("minx", minx);
                        map_top.put("miny", maxy);
                        map_top.put("maxy", begin_maxy);
                        map_top.put("maxx", begin_maxx);

                        if (begin_maxy < maxy && begin_miny > miny) {

                            newcacheExtentObj.add(map_left);

                        } else if (begin_maxy < maxy && begin_miny <= miny) {

                            newcacheExtentObj.add(map_left);


                            newcacheExtentObj.add(map_bottom);


                        } else if (begin_maxy >= maxy && begin_miny <= miny) {

                            newcacheExtentObj.add(map_left);

                            newcacheExtentObj.add(map_bottom);

                            newcacheExtentObj.add(map_top);





                        } else if (begin_maxy >= maxy && begin_miny > miny) {


                            newcacheExtentObj.add(map_left);
                            newcacheExtentObj.add(map_top);


                        }
                    } else if (begin_maxx >= maxx) {

                        map_bottom.put("minx", minx);
                        map_bottom.put("miny", begin_miny);
                        map_bottom.put("maxy", miny);
                        map_bottom.put("maxx", maxx);

                        map_top.put("minx", minx);
                        map_top.put("miny", maxy);
                        map_top.put("maxy", begin_maxy);
                        map_top.put("maxx", maxx);

                        if (begin_maxy < maxy && begin_miny > miny) {


                            newcacheExtentObj.add(map_left);


                            newcacheExtentObj.add(map_right);


                        } else if (begin_maxy < maxy && begin_miny <= miny) {

                            newcacheExtentObj.add(map_left);


                            newcacheExtentObj.add(map_right);


                            newcacheExtentObj.add(map_bottom);


                        } else if (begin_maxy >= maxy && begin_miny <= miny) {


                            newcacheExtentObj.add(map_left);



                            newcacheExtentObj.add(map_right);


                            newcacheExtentObj.add(map_top);

                            newcacheExtentObj.add(map_bottom);





                        } else if (begin_maxy >= maxy && begin_miny > miny) {


                            newcacheExtentObj.add(map_left);


                            newcacheExtentObj.add(map_right);

                            newcacheExtentObj.add(map_top);

                            //newcacheExtentObj.add(map_bottom);


                        }
                    }

                } else if (begin_minx > minx && begin_minx <= maxx && begin_miny <= maxy && begin_maxy >= miny) {

                    newcacheExtentObj.remove(map);




                    map_right.put("minx", maxx);
                    map_right.put("miny", begin_miny);
                    map_right.put("maxy", begin_maxy);
                    map_right.put("maxx", begin_maxx);



                    if (begin_maxx < maxx) {

                        map_bottom.put("minx", begin_minx);
                        map_bottom.put("miny", begin_miny);
                        map_bottom.put("maxy", miny);
                        map_bottom.put("maxx", begin_maxx);

                        map_top.put("minx", begin_minx);
                        map_top.put("miny", maxy);
                        map_top.put("maxy", begin_maxy);
                        map_top.put("maxx", begin_maxx);


                        if (begin_maxy < maxy && begin_miny <= miny) {

                            newcacheExtentObj.add(map_bottom);
                        } else if (begin_maxy >= maxy && begin_miny <= miny) {
                            newcacheExtentObj.add(map_bottom);
                            newcacheExtentObj.add(map_top);

                        } else if (begin_maxy >= maxy && begin_miny > miny) {
                            newcacheExtentObj.add(map_top);
                        }
                    } else if (begin_maxx >= maxx) {

                        map_bottom.put("minx", begin_minx);
                        map_bottom.put("miny", begin_miny);
                        map_bottom.put("maxy", miny);
                        map_bottom.put("maxx", maxx);

                        map_top.put("minx", begin_minx);
                        map_top.put("miny", maxy);
                        map_top.put("maxy", begin_maxy);
                        map_top.put("maxx", maxx);


                        if (begin_maxy < maxy && begin_miny > miny) {
                            newcacheExtentObj.add(map_right);

                        } else if (begin_maxy < maxy && begin_miny <= miny) {
                            newcacheExtentObj.add(map_right);
                            newcacheExtentObj.add(map_bottom);
                        } else if (begin_maxy >= maxy && begin_miny <= miny) {
                            newcacheExtentObj.add(map_right);
                            newcacheExtentObj.add(map_bottom);
                            newcacheExtentObj.add(map_top);


                        } else if (begin_maxy >= maxy && begin_miny > miny) {
                            newcacheExtentObj.add(map_right);
                            newcacheExtentObj.add(map_top);
                        }
                    }

                }
            }
            return boundsExtent(bounds_array, newcacheExtentObj, n + 1);
        }

        //
    }

    private void mapCacheControl(Map<String, Object> params) {
        int layerlevel = Integer.parseInt(params.get("layerlevel").toString());
        int layerid = Integer.parseInt(params.get("layerid").toString());
        int taskid = Integer.parseInt(params.get("taskid").toString());
        int layerlevelid=Integer.parseInt(params.get("layerlevelid").toString());
        boolean isenforce=Boolean.parseBoolean(params.get("isenforce").toString());
        boolean iscache=Boolean.parseBoolean(params.get("iscache").toString());
        
        threadMap.put(taskid, new ArrayList<Thread>());
        Map<String, Double> origin_extent = new HashMap<String, Double>();
        origin_extent.put("minx", Double.parseDouble(params.get("minx").toString()));
        origin_extent.put("maxy", Double.parseDouble(params.get("maxy").toString()));
        origin_extent.put("maxx", Double.parseDouble(params.get("maxx").toString()));
        origin_extent.put("miny", Double.parseDouble(params.get("miny").toString()));

        ArrayList<Map<String, Double>> CacheExtentObj = new ArrayList<Map<String, Double>>();
        CacheExtentObj.add(origin_extent);


        Dbtiandtu tddao = new Dbtiandtu();
        Map<String, Object> historyObj = isenforce?null:tddao.mapHistory(layerlevelid);
        //List<Thread> list = new ArrayList<Thread>();
        //params.put("threadlist", list);
        if(historyObj==null){
              mapCache(params);
        }
        else if (Boolean.parseBoolean(historyObj.get("isLevel").toString())) {
            CacheExtentObj = boundsExtent((ArrayList<Map<String, Object>>) historyObj.get("bounds_array"), CacheExtentObj, 0);
            for (Map<String, Double> map : CacheExtentObj) {

                Map<String, Object> url_params = new HashMap<String, Object>();

                for (int i = 0; i < params.keySet().toArray().length; i++) {
                    String key = params.keySet().toArray()[i].toString();
                    url_params.put(key, params.get(key));
                }

                url_params.put("minx", map.get("minx"));
                url_params.put("maxx", map.get("maxx"));
                url_params.put("miny", map.get("miny"));
                url_params.put("maxy", map.get("maxy"));
                mapCache(url_params);
            }
        } else {
            mapCache(params);
        }
        tddao.updateTaskState(TaskState.Begin.getState(), taskid);
        tddao.makeTaskTiles(taskid);
        /**
        for (Thread t : threadMap.get(taskid)) {
            try {
                t.join();
            } catch (InterruptedException e) {
                log.debug(e.getMessage());
            }
        }
        **/
        //tddao.updateTaskState(TaskState.end.getState(), taskid);

    }

    
    public int prepareDownTiles(Map<String,Object>params){
     Dbtiandtu tddao = new Dbtiandtu();
     return tddao.downTilesPrepare(params);
    
    }
    
    public void TilesToImg(Map<String, Object> params,int imgid){
    
             Dbtiandtu tddao = new Dbtiandtu();
        tddao.updateDownState(TaskState.Begin.getState(), imgid);
        
            int layerlevel = (Integer.parseInt( params.get("layerlevel").toString()));
        double minx = (Double.parseDouble(params.get("minx").toString()));
        double maxy = (Double.parseDouble(params.get("maxy").toString()));
        double maxx = (Double.parseDouble(params.get("maxx").toString()));
        double miny = (Double.parseDouble(params.get("miny").toString()));
        
        String filename=String.valueOf(System.currentTimeMillis());
        //int taskid = Integer.parseInt(params.get("taskid").toString());
        //int layerid=(Integer) params.get("layerid");

        Config scaleconfig = Config.getConfig("config.properties");
        String[] scales = scaleconfig.getValue("tiandituresolution").split(",");
        String[] scale_resolution = scales[layerlevel - 1].split(":");
        double resolution = Double.parseDouble(scale_resolution[0]);
        Map<String, Double> caculateParams = calculateGridParams(resolution, minx, maxy);

        double tilelon = caculateParams.get("tilelon");
        double tilelat = caculateParams.get("tilelat");
        double tileoffsetlon = caculateParams.get("tileoffsetlon");
        double tileoffsetlat = caculateParams.get("tileoffsetlat");
        double startLon = tileoffsetlon;
        int unit_width=0;
        int unit_height=0;
        
        ArrayList<ArrayList<BufferedImage>> list =new ArrayList();
        do {
            tileoffsetlon = startLon;
           ArrayList<BufferedImage> list_row =new ArrayList();
            do {
               double tileoffsetlon_temp = tileoffsetlon;
                double tileoffsetlat_temp = tileoffsetlat;
                
                  double coef = 360 / Math.pow(2, (Integer.parseInt( params.get("layerlevel").toString())));
                 long x_num = Math.round((tileoffsetlon_temp - topTileFromX) / coef);
                    long y_num = Math.round((topTileFromY - (tileoffsetlat_temp + tilelat)) / coef);
                    
                    params.put("maplayerid", params.get("maplayer"));
                     params.put("L",  params.get("layerlevel"));
                      params.put("X",  x_num);
                       params.put("Y",  y_num);
                       Dbtiandtu dbread = new Dbtiandtu();
                       byte[] imgfile=dbread.readTile(params);
                       if(imgfile==null){
                             String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                             String errorPath = path.split("WEB-INF")[0] +File.separator+"img" + File.separator+"nothing.png";
                             imgfile=ErrorFunc.errorMapTitle(errorPath);
                       }
                              
                         InputStream is = new ByteArrayInputStream(imgfile);  
                try {
                    BufferedImage image_unit= ImageIO.read(is);
                    
                     int width = image_unit.getWidth();// 图片宽度  
            int height = image_unit.getHeight();// 图片高度  
           // image_unit.getProperty("")
            unit_height=height;
            unit_width=width;
            //int[] imagenit_array= new int[width * height];// 从图片中读取RGB  
            //imagenit_array = image_unit.getRGB(0, 0, width, height, imagenit_array, 0, width);  
                    list_row.add(image_unit);
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(Imptianditu.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
                    
                    
                    
                tileoffsetlon += tilelon;
            } while (tileoffsetlon <= maxx);
            list.add(list_row);
            tileoffsetlat -= tilelat;
        } while (tileoffsetlat >= miny);
        
        int total_width=list.isEmpty()?0:list.get(0).size()*unit_width;
        int total_height=list.isEmpty()?0:list.size()*unit_height;
        
        BufferedImage imageResult = new BufferedImage(total_width,total_height,BufferedImage.TYPE_INT_RGB);  
         Graphics g = imageResult.getGraphics();
         g.fillRect(0,0,total_width,total_height);
        
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.get(i).size();j++){

//合并图片
g.drawImage(list.get(i).get(j),j*unit_width, i*unit_height, unit_width, unit_height,null);
//g.drawImage(image1,23,0,null);

//g.setColor(Color.red); 
//g.drawString("OK",10,15);


                
                   // imageResult.setRGB(j*unit_width, i*unit_height, unit_width, unit_height, list.get(i).get(j), 0, unit_width);
            }
        }
        g.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
            try {  
                boolean flag = ImageIO.write(imageResult, "png", out);
                 tddao.makeDownImg(imgid, out.toByteArray());
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(Imptianditu.class.getName()).log(Level.SEVERE, null, ex);
            }
       
         File outFile = new File("/home/jack/"+filename+".png");  
        try {
            ImageIO.write(imageResult, "png", outFile);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Imptianditu.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    
    }
    
    public String makeDownTiles(Map<String, Object> params){
        
        int imgid=prepareDownTiles(params);
        
        if(imgid>0){
         
            TilesToImg(params,imgid);
        }
    
        
        
        return "";
    
    }
    
    public void mapCache(Map<String, Object> params) {
        /*
         * layerlevelid,layerlevel,x,y,ltlon,ltlat,rblon,rblat,img
         * */

        int layerlevel = (Integer.parseInt( params.get("layerlevel").toString()));
        double minx = (Double.parseDouble(params.get("minx").toString()));
        double maxy = (Double.parseDouble(params.get("maxy").toString()));
        double maxx = (Double.parseDouble(params.get("maxx").toString()));
        double miny = (Double.parseDouble(params.get("miny").toString()));
        //int taskid = Integer.parseInt(params.get("taskid").toString());
        //int layerid=(Integer) params.get("layerid");



        Config scaleconfig = Config.getConfig("config.properties");
        String[] scales = scaleconfig.getValue("tiandituresolution").split(",");
        String[] scale_resolution = scales[layerlevel - 1].split(":");
        double resolution = Double.parseDouble(scale_resolution[0]);
        Map<String, Double> caculateParams = calculateGridParams(resolution, minx, maxy);

        double tilelon = caculateParams.get("tilelon");
        double tilelat = caculateParams.get("tilelat");
        double tileoffsetlon = caculateParams.get("tileoffsetlon");
        double tileoffsetlat = caculateParams.get("tileoffsetlat");
        double startLon = tileoffsetlon;

        do {
            tileoffsetlon = startLon;
            do {
                double tileoffsetlon_temp = tileoffsetlon;
                double tileoffsetlat_temp = tileoffsetlat;
                
                tiltodbPre(params, tileoffsetlon_temp, tileoffsetlat_temp,
                        tilelat, tilelon, scaleconfig);
                
                /**全线程
                GetMapThread mp = new GetMapThread(params, tileoffsetlon_temp, tileoffs etlat_temp,
                        tilelat, tilelon, scaleconfig);
                Thread cacheThrad = new Thread(mp);
                threadMap.get(taskid).add(cacheThrad);
                //((List<Thread>) params.get("threadlist")).add(cacheThrad);
                cacheThrad.start();**/
                tileoffsetlon += tilelon;
            } while (tileoffsetlon <= maxx);
            tileoffsetlat -= tilelat;
        } while (tileoffsetlat >= miny);
        
        
    }

    class MakeTileThread implements Runnable{
        private Map<String, Object> params = null;
        private int taskid=0;
        public MakeTileThread(Map<String, Object> pa,int id){
             this.params =pa;
             this.taskid=id;
        
        }

        @Override
        public void run() {
             mapCacheControl(params);
        }
    
    }
    
    
    class DownTileThread implements Runnable{
        private Map<String, Object> params = null;
        public DownTileThread(Map<String, Object> pa){
             this.params =pa;
        
        }

        @Override
        public void run() {
             makeDownTiles(params);
        }
    
    }
    
    
    
    
    
    
    
    /**
     * private void sigleThread(Map<String, Object>params,double
     * tileoffsetlon,double tileoffsetlat, double tilelat,double tilelon,double
     * scaleconfig,){
     *
     * }*
     */
    
    
    class GetMapThread implements Runnable {

        private int layerlevel = 0;
        private double tileoffsetlon = 0;
        private double tileoffsetlat = 0;
        private double tilelat = 0;
        private double tilelon = 0;
        private Config scaleconfig = null;
        private Map<String, Object> params = null;

        public void run() {

            Map<String, Object> url_params = new HashMap<String, Object>();

            for (int i = 0; i < params.keySet().toArray().length; i++) {
                String key = params.keySet().toArray()[i].toString();
                url_params.put(key, params.get(key));
            }
            int taskid = (Integer.parseInt(params.get("taskid").toString()));

            Dbtiandtu tddao = new Dbtiandtu();
            double coef = 360 / Math.pow(2, layerlevel);
            long x_num = Math.round((tileoffsetlon - topTileFromX) / coef);
            long y_num = Math.round((topTileFromY - (tileoffsetlat + tilelat)) / coef);
            String layerlabel = params.get("layerlabel").toString();
            String imgUrl = getRandomServer(scaleconfig,"tiandituserver") + "?T=" + layerlabel + "&X=" + x_num + "&Y=" + y_num + "&L=" + layerlevel;
            ImplToPg imgObj = new ImplToPg();
            log.debug(imgUrl);

            byte[] imgBytes = imgObj.readUrlmap(imgUrl);
            if(imgBytes==null){
                url_params.put("issucess", false);
                
                tddao.updateTaskState(TaskState.fail.getState(), taskid);
                
            }else{
                  url_params.put("issucess", true);
            }
            url_params.put("x", x_num);
            url_params.put("y", y_num);
            url_params.put("tileminx", tileoffsetlon);
            url_params.put("tilemaxx", tileoffsetlon + tilelon);
            url_params.put("tilemaxy", tileoffsetlat + tilelat);
            url_params.put("tileminy", tileoffsetlat);

            tddao.mapCache(url_params, imgBytes);

        }

        public GetMapThread(Map<String, Object> params, double tileoffsetlon,
                double tileoffsetlat, double tilelat, double tilelon, Config scaleconfig) {
            this.params = params;
            this.tilelat = tilelat;
            this.tilelon = tilelon;
            this.tileoffsetlat = tileoffsetlat;
            this.tileoffsetlon = tileoffsetlon;
            this.layerlevel = (Integer.parseInt( params.get("layerlevel").toString()));
            this.scaleconfig = scaleconfig;


        }
    }

    private void tiltodbPre(Map<String, Object> params,double tileoffsetlon,
                double tileoffsetlat, double tilelat, double tilelon, Config scaleconfig){
    
         Map<String, Object> url_params = new HashMap<String, Object>();
         int taskid = Integer.parseInt(params.get("taskid").toString());
         

            for (int i = 0; i < params.keySet().toArray().length; i++) {
                String key = params.keySet().toArray()[i].toString();
                url_params.put(key, params.get(key));
            }

            Dbtiandtu tddao = new Dbtiandtu();
            
            double coef = 360 / Math.pow(2, (Integer.parseInt( params.get("layerlevel").toString())));
            long x_num = Math.round((tileoffsetlon - topTileFromX) / coef);
            long y_num = Math.round((topTileFromY - (tileoffsetlat + tilelat)) / coef);
 
            url_params.put("x", x_num);
            url_params.put("y", y_num);
            url_params.put("tileminx", tileoffsetlon);
            url_params.put("tilemaxx", tileoffsetlon + tilelon);
            url_params.put("tilemaxy", tileoffsetlat + tilelat);
            url_params.put("tileminy", tileoffsetlat);

            tddao.mapCachePrepare(url_params);
            
            

        
    
    }
    
    public static String getRandomServer(Config scaleconfig,String servername) {
        String[] serverurls = scaleconfig.getValue(servername).split(",");
        int b = (int) (Math.random() * (serverurls.length - 1));
        return serverurls[b];
    }
    
    public static String  getRandomServer(String servername) {
        String[] serverurls = servername.split(";");
        int b = (int) (Math.random() * (serverurls.length - 1));
        return serverurls[b];
    
    }

    private Map<String, Double> calculateGridParams(double resolution, double minx, double maxy) {
        Map<String, Double> params = new HashMap<String, Double>();
        double tilelon = resolution * tileSizeWidth;
        double tilelat = resolution * tileSizeHeight;

        double offsetlon = minx - originLon;
        double tilecol = Math.floor(offsetlon / tilelon);
        double tileoffsetlon = originLon + tilecol * tilelon;

        double offsetlat = maxy - (originLat + tilelat);
        double tilerow = Math.ceil(offsetlat / tilelat);
        double tileoffsetlat = originLat + tilerow * tilelat;
        params.put("tilelon", tilelon);
        params.put("tilelat", tilelat);
        params.put("tileoffsetlon", tileoffsetlon);
        params.put("tileoffsetlat", tileoffsetlat);

        return params;


    }

    public static void main(String[] args) {

        Imptianditu test = new Imptianditu();
        Map<String, Object> params = new HashMap<String, Object>();
        /**
        params.put("mapname", "天地图线划地图");
        params.put("mapower", "tianditu");
        params.put("maplabel", "xhdt");
        params.put("spatialreference", "wgs84");
        params.put("projection", "经纬度");
        params.put("layerlabel", "B0627_EMap1112");
        params.put("layername", "1:100万矢量地图");
        params.put("layerlevel", 11);
**/

         params.put("layerlevel", 13);
          params.put("maplayer", 2);
        params.put("maxx", 120.5);
        params.put("minx", 120);
        params.put("maxy", 30.4);
        params.put("miny", 30.1);

        //test.maptodb(params);
        
       test. makeDownTiles(params);

    }
}
