package mapcatche.conmmon;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/*
 * 文件类
 * */
public class FileFunc {

    private static final Logger log = Logger.getLogger(FileFunc.class);

    public static void  saveFilefromBytes(String path,byte[] imgbyte){
        
        File file=new File(path);
        if(file.exists()){
            
            file.delete();
        
        }
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
    
        FileOutputStream fos=null;   
        try{   
            fos=new FileOutputStream(path,true);   
            fos.write(imgbyte);   
            fos.flush();     
        }     
        catch(Exception e){   
            //e.printStackTrace();   
            log.debug(e.getMessage());
            //Logger.getInstance().logPrint("writeObject", "写byte数组出错:"+e.getMessage());   
        }   
        finally{   
            try{   
                fos.close();   
            }   
            catch(IOException iex){}   
        }   
    }
    
    
    
    @SuppressWarnings("finally")
    public static byte[] readFileByBytes(String fileName) {

        byte[] buf = new byte[1024];
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream in = null;
        try {

            in = new FileInputStream(fileName);
            bis = new BufferedInputStream(in);
            for (int len = 0; (len = bis.read(buf)) != -1;) {
                baos.write(buf, 0, len);
            }


        } catch (Exception e1) {
            e1.printStackTrace();
            log.debug(e1.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException e1) {
                    log.debug(e1.getMessage());
                }
            }
            return baos.toByteArray();
        }
    }

    public static String getXMLString(String filePath) {
       StringBuffer sb=new StringBuffer();
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(filePath));

            while (true) {

                line = br.readLine();

                if (line == null) {

                    break;
                }
                sb.append(line + "\n");
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return sb.toString();

    }
    
    

}
