package mapcatche.business;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mapcatche.conmmon.Config;

public class Test {
	public static void main(String[] args) throws Exception{
		String a="http://tile0.tianditu.com/DataServer?T=siwei0608&X=${x}&Y=${y}&L=${z}";
                
                Map<String,Object> arguments = new HashMap<String, Object>(){{
            put("x", "tom");
            put("y", 23);
            put("z", 231);
        }};

                                    String s = format(a,arguments);
                                    System.out.println(s);
	  }
	public static void c(double a){
		a=3.0;
	}
        
         public static String format(String pattern, Map<String,Object> arguments){
        String formatedStr = pattern;
        for (String key : arguments.keySet()) {
            formatedStr = formatedStr.replaceAll("\\$\\{"+key+"\\}", arguments.get(key).toString());
        }
        return formatedStr;
    }

       
}

class SelectThread implements Runnable{
	
	  public void run(){
		  
		  for(int i=0;i<1000;i++){
			  System.out.println(i);
		  }
		  
	  }}

class R implements Runnable{
	  //private int x = 0;
	  public void run(){
		  for(int i=0;i<100;i++){
			  System.out.println(i);
		  }
		  try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	}