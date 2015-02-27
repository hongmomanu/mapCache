package mapcatche.implmapfunc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class ImplToPg {

    private static final Logger log = Logger.getLogger(ImplToPg.class);

    /**
     * ���Դ���*
     */
    public static void main(String[] args) {
        ImplToPg a = new ImplToPg();
        String imgurl = "http://tile5.tianditu.com/DataServer?T=B0627_EMap1112&X=1706&Y=341&L=11";
        //imgurl="http://www.tianditu.cn/images/openlayers/img/panzoombar_blue/zoom3.png";
        log.debug(a.readUrlmap("http://tile5.tianditu.com/DataServer?T=B0627_EMap1112&X=1706&Y=341&L=11").length);
    }

    /**
     * 读取网络文件*
     */
    public byte[] readUrlmap(String imgurl) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream bis = null;
        HttpURLConnection urlconnection = null;
        URL url = null;
        byte[] buf = new byte[1024];
        try {
            
            url = new URL(imgurl);
            urlconnection = (HttpURLConnection) url.openConnection();
            urlconnection.addRequestProperty("User-Agent", "Mozilla/4.76");
            urlconnection.setConnectTimeout(10000);
            urlconnection.setReadTimeout(10000);
            urlconnection.connect();
            bis = new BufferedInputStream(urlconnection.getInputStream());
            for (int len = 0; (len = bis.read(buf)) != -1;) {
                baos.write(buf, 0, len);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            log.debug(e.getMessage());
            return null;
        } finally {
            try {
                urlconnection.disconnect();
                if (bis == null) {
                    return null;
                } else {
                    bis.close();
                    return baos.toByteArray();
                }
            } catch (IOException ignore) {
                log.debug(ignore.getMessage());
                return null;
            }
        }
    }
}
