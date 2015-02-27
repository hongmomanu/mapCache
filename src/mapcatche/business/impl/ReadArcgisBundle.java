/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcatche.business.impl;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import mapcatche.business.dbdao.Dbtiandtu;
import mapcatche.business.intf.Readmap;
import mapcatche.conmmon.FileFunc;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

/**
 *
 * @author jack
 */
public class ReadArcgisBundle implements Readmap {

    @Override
    public byte[] maptile(Map<String, Object> parmas) {
            int level = Integer.parseInt(parmas.get("L").toString());
        int row = Integer.parseInt(parmas.get("Y").toString());
        int col = Integer.parseInt(parmas.get("X").toString());
        int maplayerid= parmas.get("maplayerid")==null?-1:Integer.parseInt(parmas.get("maplayerid").toString());
        Dbtiandtu dbread = new Dbtiandtu();
        String maplabel=parmas.get("maplabel")==null?dbread.getmaplable(maplayerid):parmas.get("maplabel").toString();
         
        String bundlesDir=maplabel+"_alllayers";

        
        byte[] result = null;
        String l = "0" + level;
        int lLength = l.length();
        if (lLength > 2) {
            l = l.substring(lLength - 2);
        }
        l = "L" + l;
        

        int rGroup = 128 * (row / 128);   
        String r = Integer.toHexString(rGroup);   
        int rLength = r.length();   
        if (rLength < 4) { 
            for(int i=0;i<4-rLength;i++)
                { r="0"+r; 
                }
        }   
        r = "R" + r;    
        int cGroup = 128 * (col / 128);   
        String c =Integer.toHexString(cGroup);    
        int cLength = c.length();    
        if (cLength < 4) { 
            for(int i=0;i<4-cLength;i++){ 
                c="0"+c;
            }   
        }
        c = "C" + c;    
        String bundleBase = String .format("%s/%s/%s%s", bundlesDir, l, r, c); 
        String bundlxFileName = bundleBase + ".bundlx"; 
        String bundleFileName = bundleBase + ".bundle";     
        int index = 128 * (col - cGroup) + (row - rGroup);   
        try{
        FileInputStream isBundlx = new FileInputStream(bundlxFileName);    
        isBundlx.skip(16 + 5 * index);
        
        
        byte[] buffer = new byte[5];  
        isBundlx.read(buffer);  
        long offset = (long) (buffer[0] & 0xff) + (long) (buffer[1] & 0xff) * 256 
                + (long) (buffer[2] & 0xff) * 65536 + (long) (buffer[3] & 0xff) * 16777216 
                + (long) (buffer[4] & 0xff) * 4294967296L;  
        FileInputStream isBundle = new FileInputStream(bundleFileName);  
        isBundle.skip(offset);  
        byte[] lengthBytes = new byte[4];   
        isBundle.read(lengthBytes);  
        int length = (int) (lengthBytes[0] & 0xff)   + (int) (lengthBytes[1] & 0xff) * 256 
                + (int) (lengthBytes[2] & 0xff) * 65536   + (int) (lengthBytes[3] & 0xff) * 16777216;    
        
          result = new byte[length];
            isBundle.read(result);
        isBundle.close();
        }catch(Exception e){
        
        
        }
       

        return result;
    }

    public String getcacheconfig(String maplabel){
        //System.out.println(maplabel);
        String xml=FileFunc.getXMLString(maplabel+"conf.xml");
        XMLSerializer xmlSerializer=new XMLSerializer();
        net.sf.json.JSON json=xmlSerializer.read(xml);

        return json.toString();
        
    }
    
    public static void main(String[] args){
        ReadArcgisBundle a=new ReadArcgisBundle();
        a.getcacheconfig("/home/jack/data/arcgiscache/tzmzmapcopy/");
    }
    @Override
    public String maptree(Map<String, Object> parmas) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String maptask(Map<String, Object> parmas) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String mapower(int mapid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
