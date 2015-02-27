package mapcatche.conmmon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {

    private static Properties propertie = null;
    private FileInputStream inputFile;
    private String propertiesFileName = "";
    private static Config configObj = null;

    public String getValue(String key) {
        if (propertie.containsKey(key)) {
            String value = propertie.getProperty(key);
            return value;
        } else {
            return "";
        }
    }

    public static void freshObj() {
            configObj = null;
    }

    public void setValue(String itemName, String value) {
        propertie.setProperty(itemName, value);

        FileOutputStream out;
        try {
            out = new FileOutputStream(propertiesFileName); //输出流
            propertie.store(out, "Just Test");//设置属性头，如不想设置，请把后面一个用""替换掉
            out.flush();//清空缓存，写入磁盘
            out.close();//关闭输出流
        } catch (Exception ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public Map<String, String> getmapValues() {
        Map<String, String> mapvalue = new HashMap();
        Set<Object> keyset = propertie.keySet();

        for (Object object : keyset) {
            String propValue = propertie.getProperty(object.toString()).toString();
            mapvalue.put(object.toString(), propValue);

        }
        return mapvalue;

    }

    private Config(String filePath) {
        propertie = new Properties();
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        propertiesFileName = path + "/" + filePath;
        try {

            inputFile = new FileInputStream(path + "/" + filePath);
            propertie.load(inputFile);
            inputFile.close();

        } catch (FileNotFoundException ex) {
            //System.out.println("读取属性文件--->失败！- 原因：文件路径错误或者文件不存在");
            ex.printStackTrace();
        } catch (IOException ex) {
            //System.out.println("装载文件--->失败!");
            ex.printStackTrace();
        }

    }

    public static Config getConfig(String filePath) {
        if (configObj == null) {
            Logger.getLogger(Config.class.getName()).info("重新加载配置文件");
                    
            configObj = new Config(filePath);
        }
        return configObj;
    }
}
