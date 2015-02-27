/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import mapcatche.business.dbdao.Dbtiandtu;
import mapcatche.business.intf.Impmap;
import mapcatche.conmmon.Config;
import mapcatche.conmmon.ErrorFunc;

/**
 *
 * @author jack
 */
public class Impgoogle extends Imptianditu {
    
    private static double maxResolution=156543.0339;
    private static double maxExtentLeft=-20037508.34;
     private static double maxExtentTop=20037508.34;
     
     
     public static  double[] lonlat2mercator(double  lon,double lat){
         
         double mercatorx = lon *maxExtentTop/180;
         double mercatory= (Math.log(Math.tan((90+lat)*Math.PI/360))/(Math.PI/180))*20037508.34/180;
      
         double [] mecator={mercatorx,mercatory};
         
         return mecator;
     
     }
     
public static  double[] mercator2lonlat(double mercatorx ,double mercatory){
    
       double lon =mercatorx/20037508.34*180;
    double lat = mercatory/20037508.34*180;
    lat= 180/Math.PI*(2*Math.atan(Math.exp(lat*Math.PI/180))-Math.PI/2);
    
    double[] lonlat={lon,lat};
    return lonlat;
    

    
    
}


    @Override
    public void TilesToImg(Map<String, Object> params,int imgid){
        
          Dbtiandtu tddao = new Dbtiandtu();
        tddao.updateDownState(TaskState.Begin.getState(), imgid);
        
            int layerlevel = (Integer.parseInt( params.get("layerlevel").toString()));
        double minx = (Double.parseDouble(params.get("minx").toString()));
        double maxy = (Double.parseDouble(params.get("maxy").toString()));
        double maxx = (Double.parseDouble(params.get("maxx").toString()));
        double miny = (Double.parseDouble(params.get("miny").toString()));
        
        //int taskid = Integer.parseInt(params.get("taskid").toString());
        //int layerid=(Integer) params.get("layerid");
        
         double[] mercatormin=lonlat2mercator(minx,miny);
        double[] mercatormax=lonlat2mercator(maxx,maxy);
        
        
         double mercatorminx = mercatormin[0];
         double mercatormaxx =mercatormax[0];
         
         double mercatorminy=mercatormin[1];
         double mercatormaxy= mercatormax[1];
   
        
        
        
        //int taskid = Integer.parseInt(params.get("taskid").toString());
        //int layerid=(Integer) params.get("layerid");
        
        long tileoffsetx=0;
        //int startx=(int) ( ( (minx +180) / 360 * (1 << layerlevel) ) + 1.5);
        double resolution=maxResolution/Math.pow(2,layerlevel);
        long startx=Math.round((mercatorminx- maxExtentLeft) /  (resolution* 256));
        
         
       
        
       // int maxx_num=(int) ( ( (maxx +180) / 360 * (1 << layerlevel) ) + 1.5);
        long maxx_num=Math.round((mercatormaxx- maxExtentLeft) /  (resolution* 256));
        //int tileoffsety=(int)((Math.log(Math.tan(Math.PI/4-miny/180*Math.PI/2))/Math.PI+1)/2*(1<< layerlevel)+1.5);
       // int miny_num=(int)((Math.log(Math.tan(Math.PI/4-maxy/180*Math.PI/2))/Math.PI+1)/2*(1<< layerlevel)+1.5);
        
        long tileoffsety=Math.round((maxExtentTop - mercatormaxy) /  (resolution * 256));
        
        long maxy_num=Math.round((maxExtentTop - mercatorminy) /  (resolution * 256));
        
        int tilex=1;
        int tiley=1;
        
        int unit_width=0;
        int unit_height=0;
        
                
  ArrayList<ArrayList<BufferedImage>> list =new ArrayList();

        do {
            tileoffsetx = startx;
            System.out.println("开始行:"+tileoffsetx);
              ArrayList<BufferedImage> list_row =new ArrayList();
            do {
                long  tileoffsetx_temp = tileoffsetx;
                long tileoffsety_temp = tileoffsety;
                  System.out.println(tileoffsetx_temp);
                    
                    params.put("maplayerid", params.get("maplayer"));
                     params.put("L",  params.get("layerlevel"));
                      params.put("X",  tileoffsetx_temp);
                       params.put("Y",  tileoffsety_temp);
                       Dbtiandtu dbread = new Dbtiandtu();
                       byte[] imgfile=dbread.readTile(params);
                       if(imgfile==null||imgfile.length==0){
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
     
                tileoffsetx += tilex;
            } while (tileoffsetx <= maxx_num);
              list.add(list_row);
            tileoffsety += tiley;
        } while (tileoffsety <= maxy_num);
        
     
        
        
        int total_width=list.isEmpty()?0:list.get(0).size()*unit_width;
        int total_height=list.isEmpty()?0:list.size()*unit_height;
        
        BufferedImage imageResult = new BufferedImage(total_width,total_height,BufferedImage.TYPE_INT_RGB);  
         Graphics g = imageResult.getGraphics();
         g.fillRect(0,0,total_width,total_height);
        
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.get(i).size();j++){

//合并图片
g.drawImage(list.get(i).get(j),j*unit_width, i*unit_height, unit_width, unit_height,null);
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
       
        


}

    @Override
     public void mapCache(Map<String, Object> params) {
        
           /*
         * layerlevelid,layerlevel,x,y,ltlon,ltlat,rblon,rblat,img
         * */

        int layerlevel = (Integer.parseInt( params.get("layerlevel").toString()));
        double minx = (Double.parseDouble(params.get("minx").toString()));
        double maxy = (Double.parseDouble(params.get("maxy").toString()));
        double maxx = (Double.parseDouble(params.get("maxx").toString()));
        double miny = (Double.parseDouble(params.get("miny").toString()));
        
        
        double[] mercatormin=lonlat2mercator(minx,miny);
        double[] mercatormax=lonlat2mercator(maxx,maxy);
        
        
         double mercatorminx = mercatormin[0];
         double mercatormaxx =mercatormax[0];
         
         double mercatorminy=mercatormin[1];
         double mercatormaxy= mercatormax[1];
   
        
        
        
        //int taskid = Integer.parseInt(params.get("taskid").toString());
        //int layerid=(Integer) params.get("layerid");
        
        long tileoffsetx=0;
        //int startx=(int) ( ( (minx +180) / 360 * (1 << layerlevel) ) + 1.5);
        double resolution=maxResolution/Math.pow(2,layerlevel);
        long startx=Math.round((mercatorminx- maxExtentLeft) /  (resolution* 256));
        
         
       
        
       // int maxx_num=(int) ( ( (maxx +180) / 360 * (1 << layerlevel) ) + 1.5);
        long maxx_num=Math.round((mercatormaxx- maxExtentLeft) /  (resolution* 256));
        //int tileoffsety=(int)((Math.log(Math.tan(Math.PI/4-miny/180*Math.PI/2))/Math.PI+1)/2*(1<< layerlevel)+1.5);
       // int miny_num=(int)((Math.log(Math.tan(Math.PI/4-maxy/180*Math.PI/2))/Math.PI+1)/2*(1<< layerlevel)+1.5);
        
        long tileoffsety=Math.round((maxExtentTop - mercatorminy) /  (resolution * 256));
        
        long miny_num=Math.round((maxExtentTop - mercatormaxy) /  (resolution * 256));
        
        int tilex=1;
        int tiley=1;
          Dbtiandtu tddao = new Dbtiandtu();
            Map<String, Object> url_params = new HashMap<String, Object>();
         int taskid = Integer.parseInt(params.get("taskid").toString());
         

            for (int i = 0; i < params.keySet().toArray().length; i++) {
                String key = params.keySet().toArray()[i].toString();
                url_params.put(key, params.get(key));
            }
                


        do {
            tileoffsetx = startx;
            do {
                long  tileoffsetx_temp = tileoffsetx;
                long tileoffsety_temp = tileoffsety;
                /**
                double tileminx=(tileoffsetx-1.5)/(1 << layerlevel)*360-180;
                  double tilemaxx=(tileoffsetx-1.5+1)/(1 << layerlevel)*360-180;
                  double tilemaxy=(Math.PI/4-Math.atan(Math.pow(Math.E,((tileoffsety-1.5)/(1 << layerlevel)*2-1)*Math.PI)))/(Math.PI/2)*180;
                   double tileminy=(Math.PI/4-Math.atan(Math.pow(Math.E,((tileoffsety-1.5+1)/(1 << layerlevel)*2-1)*Math.PI)))/(Math.PI/2)*180;
                   * 
                   * **/
                double tileminx= maxExtentLeft+ tileoffsetx*(resolution* 256);
                 double tilemaxx=maxExtentLeft+ (tileoffsetx+1)*(resolution* 256);
                 double tilemaxy=maxExtentTop-tileoffsety* (resolution * 256);
                  double tileminy=maxExtentTop-(tileoffsety+1)* (resolution * 256);
                  
                  double[]lonlatmin=mercator2lonlat(tileminx, tileminy);
                    double[]lonlatmax=mercator2lonlat(tilemaxx, tilemaxy);
                  
                  
               
            url_params.put("x", tileoffsetx_temp);
            url_params.put("y", tileoffsety_temp);
            url_params.put("tileminx", lonlatmin[0]);
            url_params.put("tilemaxx", lonlatmax[0]);
            url_params.put("tilemaxy", lonlatmax[1]);
            url_params.put("tileminy", lonlatmin[1]);

            tddao.mapCachePrepare(url_params);         
                tileoffsetx += tilex;
            } while (tileoffsetx <= maxx_num);
            tileoffsety -= tiley;
        } while (tileoffsety >= miny_num);
        
     
        
     
     }
    
    

    
    
}
