package mapcatche.business.dbdao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import mapcatche.business.impl.Imptianditu;
import mapcatche.business.impl.TaskState;
import mapcatche.conmmon.Config;
import mapcatche.conmmon.FileFunc;
import mapcatche.conmmon.StringHelper;
import mapcatche.implmapfunc.ImplToPg;
import mapcatche.jdbc.PostgreSql;
import org.apache.log4j.Logger;

public class Dbtiandtu {

    private static final Logger log = Logger.getLogger(Dbtiandtu.class);
    private static String mapinfoTable = "maptype";
    private static String layerTable = "layertype";
    private static String levelTable = "layerlevel";
    private static String userTable="users"; 
    private static String taskTable = "task";
    private static String downTable="imgfile";
    private static String cacheTable = "mapcatche";
    private static String serverTable="mapserver";
    //public static String cachedir=Config.getConfig("config.properties").getValue("cahcedir");
    private static long oneyear = 1000 * 60 * 60 * 24;
    private static boolean ispublic = false;
    public static int keydefault = -1;
    private static Connection conn = null;

    public static void freshobj(){
            if(conn!=null)try {
            conn.close();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
        }
            conn=null;
            
    }
    private Connection getConn() {
        try {
            if (conn == null||conn.isClosed()) {
                PostgreSql db = new PostgreSql();
                conn = db.getConn();
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
public void canceltask(int taskid){
    for (Thread t : Imptianditu.threadMap.get(taskid)) {
        //log.debug("要终止的线程:"+taskid);
        t.interrupt();
    }
}


public Map<String,Object>register(String username,String password){

     PostgreSql db = new PostgreSql();
        conn = getConn();
         Map<String,Object> map;
        map = new HashMap();

         String sql= "select count(*)  from "+ userTable+" a  where a.username='"+username+"'";
        // log.debug(sql);
         if(totalCount(sql)>0){
             map.put("issuccess", false);
             map.put("msg","用户已存在");
             return map;
         }else{
         
               String insert_sql= "insert into  "+userTable +"(username,passwd,logintime) values(?,?,?)";
        
        PreparedStatement check_pstmt = db.getPstmt(conn, insert_sql);
        try {
            Timestamp time=new Timestamp(System.currentTimeMillis());
            check_pstmt.setString(1, username);
            check_pstmt.setString(2, password);
            check_pstmt.setTimestamp(3, time);
           check_pstmt.executeUpdate();
            map.put("issuccess", true);
             map.put("usertype",1);
             map.put("logintime",time.getTime());
                     
            
         
        } catch (SQLException e) {
            log.debug(e.getMessage());
            
            db.closeConection(check_pstmt, null, conn);
             map.put("issuccess", false);
             map.put("msg",e.getMessage());
        }finally{
        return map;
        }
         
         }
     
         
    

}

public Map<String,Object> login(String username,String password){

     PostgreSql db = new PostgreSql();
        conn = getConn();

         String sql= "select id, username,passwd,nickname,usertype,logintime  from "+userTable +" a  where a.username=? and a.passwd=?";
         Map<String,Object> map=new HashMap();
         map.put("issuccess", false);
         
            PreparedStatement check_pstmt = db.getPstmt(conn, sql);
        try {
            check_pstmt.setString(1, username);
            check_pstmt.setString(2, password);
            ResultSet rs = check_pstmt.executeQuery();
              boolean flag=false;
            while (rs.next()) {
                flag=true;
                  map.put("issuccess", true);
                   map.put("username", rs.getString("username"));
                   map.put("password", rs.getString("passwd"));
                   map.put("usertype", rs.getInt("usertype"));
                   map.put("logintime", rs.getTimestamp("logintime").getTime());
                   
                   String update_sql="update " +userTable +" set logintime=? where id=?";
                     PreparedStatement pstmt = db.getPstmt(conn, update_sql);
                     pstmt.setTimestamp(1,new Timestamp(System.currentTimeMillis()));
                     pstmt.setInt(2, rs.getInt("id"));
                   pstmt.executeUpdate();
                   
            }
            if(!flag){
            map.put("msg", "用户名或者密码错误！");
            }
        


        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(check_pstmt, null, conn);
            map.put("msg", e.getMessage());
        }finally{
        
        return map;
        }
         
}

 public int deldowntask(int taskid){
    
        
     PostgreSql db = new PostgreSql();
        conn = getConn();
       
        String total_sql= "delete  from  "+downTable +" a  where a.id=?";
        
        
        PreparedStatement check_pstmt = db.getPstmt(conn, total_sql);
    
        try {
            check_pstmt.setInt(1, taskid);
           
            return check_pstmt.executeUpdate();
         
        } catch (SQLException e) {
            log.debug(e.getMessage());
            
            db.closeConection(check_pstmt, null, conn);
            return -1;
        }

        
    }
   

    public int deltask(int taskid){
    
        
     PostgreSql db = new PostgreSql();
        conn = getConn();
       
        String total_sql= "delete  from  "+taskTable +" a  where a.id=?";
        String del_sql="delete from " +cacheTable+ " b where b.taskid=?";
        
        PreparedStatement check_pstmt = db.getPstmt(conn, total_sql);
          PreparedStatement del_pstmt = db.getPstmt(conn, del_sql);
        try {
            check_pstmt.setInt(1, taskid);
            del_pstmt.setInt(1, taskid);
            check_pstmt.executeUpdate();
            return del_pstmt.executeUpdate();
         
        } catch (SQLException e) {
            log.debug(e.getMessage());
            
            db.closeConection(check_pstmt, null, conn);
            return -1;
        }

        
    }


    public void makeTaskTiles(int taskid){


        PostgreSql db = new PostgreSql();
        conn = getConn();

        Config config = Config.getConfig("config.properties");
        int thradnums=Integer.parseInt(config.getValue("threadnum").toString());
        ArrayList<ArrayList<Long>> list=new ArrayList<ArrayList<Long>>();
        for(int i=0;i<thradnums;i++){
            ArrayList<Long> child_list=new ArrayList();
            list.add(child_list);
        }


        String total_sql= "select min(b.id) as min ,max(b.id) as max from "+taskTable +" a,"+cacheTable+" b where a.id=b.taskid"
                + " and a.id=? and b.issucess=false";
        PreparedStatement check_pstmt = db.getPstmt(conn, total_sql);
        try {
            check_pstmt.setInt(1, taskid);
            ResultSet rs = check_pstmt.executeQuery();
            long max=0;
            long min=0;
            while (rs.next()) {
                //long cacheid=rs.getLong("id");
                max=rs.getLong("max");
                min=rs.getLong("min");
            }
            long space= (max-min)/thradnums;
            for(int i=0;i<thradnums;i++){

                list.get(i).add(min+i*thradnums);
                if(i==thradnums-1)list.get(i).add(max) ;
                else list.get(i).add(min+(i+1)*thradnums);


            }




        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(check_pstmt, null, conn);
        }



        List<Thread> thread_list = new ArrayList<Thread>();

        for(int i=0;i<thradnums;i++){

            if(list.get(i).size()>0){
                finishCacheThread refiT = new finishCacheThread(list.get(i),taskid);
                Thread t = new Thread(refiT);
                thread_list.add(t);
                Imptianditu.threadMap.get(taskid).add(t);
                t.start();

            }


        }


        for(Thread t : thread_list) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String result_sql="select count(*) from "+cacheTable+" where taskid=? and issucess=false";
        PreparedStatement rs_pstmt = db.getPstmt(conn, result_sql);
        try {
            rs_pstmt.setInt(1, taskid);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
        }

        //log.debug("ok,all thread end!");

        if(db.ExistId(rs_pstmt)==0){
            updateTaskState(TaskState.end.getState(), taskid);
        }else{
            updateTaskState(TaskState.fail.getState(), taskid);
        }



    }


    public void getTasktiles(int taskid){
     
     
     
     PostgreSql db = new PostgreSql();
        conn = getConn();

     Config config = Config.getConfig("config.properties");
      int thradnums=Integer.parseInt(config.getValue("threadnum").toString());
      ArrayList<ArrayList<Long>> list=new ArrayList<ArrayList<Long>>(); 
      for(int i=0;i<thradnums;i++){
      ArrayList<Long> child_list=new ArrayList();
      list.add(child_list);
      }
      
      
     String total_sql= "select b.id from "+taskTable +" a,"+cacheTable+" b where a.id=b.taskid"
             + " and a.id=? and b.issucess=false";
        PreparedStatement check_pstmt = db.getPstmt(conn, total_sql);
        try {
            check_pstmt.setInt(1, taskid);
            ResultSet rs = check_pstmt.executeQuery();
              
            while (rs.next()) {
              long cacheid=rs.getLong("id");
                  
                  int b = (int) (Math.random() * (thradnums - 1));
                  list.get(b).add(cacheid);
               
                
            }
        
            


        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(check_pstmt, null, conn);
        }

     
     
      List<Thread> thread_list = new ArrayList<Thread>();
      
      for(int i=0;i<thradnums;i++){
            
          if(list.get(i).size()>0){
              finishCacheThread refiT = new finishCacheThread(list.get(i),taskid);
              Thread t = new Thread(refiT);
               thread_list.add(t);
               Imptianditu.threadMap.get(taskid).add(t);
               t.start();
          
          }
             
          
      }
      
      
      for(Thread t : thread_list) {
                try {
                    t.join();
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            String result_sql="select count(*) from "+cacheTable+" where taskid=? and issucess=false";
             PreparedStatement rs_pstmt = db.getPstmt(conn, result_sql);
            try {
            rs_pstmt.setInt(1, taskid);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            //log.debug("ok,all thread end!");
        
            if(db.ExistId(rs_pstmt)==0){
            updateTaskState(TaskState.end.getState(), taskid);
            }else{
            updateTaskState(TaskState.fail.getState(), taskid);
            }
            
     
    }
    public void refinishtask(int taskid){
    
        PostgreSql db = new PostgreSql();
        conn = getConn();

        String check_sql = "select b.*,c.layerlabel,d.layerlevel from " + taskTable + " a,"+cacheTable+" b,"+layerTable+" "
                + " c,"+levelTable+" d  where b.taskid=a.id and a.levelid=d.id and "
                + "d. layerid=c.id and b.issucess=false and a.id=?";
        PreparedStatement check_pstmt = db.getPstmt(conn, check_sql);
        try {
            check_pstmt.setInt(1, taskid);
            ResultSet rs = check_pstmt.executeQuery();
              Config scaleconfig = Config.getConfig("config.properties");
              List<Thread> list = new ArrayList<Thread>();
            while (rs.next()) {
              String layerlabel=rs.getString("layerlabel");
              int x_num=rs.getInt("x");
              int y_num=rs.getInt("y");
              int layerlevel=rs.getInt("layerlevel");
              int cacheid=rs.getInt("id");
                  String imgUrl = Imptianditu.getRandomServer(scaleconfig,"tiandituserver") + "?T=" + layerlabel + "&X=" + x_num + "&Y=" + y_num + "&L=" + layerlevel;
                  log.debug(imgUrl);
                  //log.debug(cacheid);
               RefinishCacheThread refiT = new RefinishCacheThread(cacheid,imgUrl);
                        Thread t = new Thread(refiT);
                        list.add(t);
                        t.start();
                
            }
            for(Thread t : list) {
                try {
                    t.join();
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            String result_sql="select count(*) from "+cacheTable+" where taskid=? and issucess=false";
            PreparedStatement rs_pstmt = db.getPstmt(conn, result_sql);
            rs_pstmt.setInt(1, taskid);
            if(db.ExistId(rs_pstmt)==0){
            updateTaskState(TaskState.end.getState(), taskid);
            }
            


        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(check_pstmt, null, conn);
        }

    }
    
    public int mapresourcetodb(Map<String, Object> params) {

        PostgreSql db = new PostgreSql();
        conn = getConn();
        String type = params.get("type").toString();
        int treelevel = Integer.parseInt(params.get("treelevel").toString());
        String keyid = params.get("keyid").toString();
        String sql = "";
        int returnnum = 0;
        if(treelevel == -1){
        
              sql = "insert  into " + serverTable + " (mapower ,owername,spatialreference,projection,updatetime,maptype) values (?,?,?,?,?,?)  ";
                PreparedStatement pstmt = db.getPstmt(conn, sql);
                try {
                    pstmt.setString(1, params.get("name").toString());
                    pstmt.setString(2, params.get("text").toString());
                    pstmt.setString(3, params.get("spatialreference").toString());
                    pstmt.setString(4, params.get("projection").toString());
                    pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    pstmt.setInt(6, Integer.parseInt(params.get("maptype").toString()));
                    //pstmt.setInt(7, Integer.parseInt(params.get("keyid").toString()));
                    returnnum = pstmt.executeUpdate();
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        else if (treelevel == 0) {
            if (type.trim().equals("update")) {
                sql = "update " + serverTable + " set mapower=? ,owername=?,spatialreference=?,projection=?,updatetime=?,maptype=?  where id=?";
                PreparedStatement pstmt = db.getPstmt(conn, sql);
                try {
                    pstmt.setString(1, params.get("name").toString());
                    pstmt.setString(2, params.get("text").toString());
                    pstmt.setString(3, params.get("spatialreference").toString());
                    pstmt.setString(4, params.get("projection").toString());
                    pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    pstmt.setInt(6, Integer.parseInt(params.get("maptype").toString()));
                    pstmt.setInt(7, Integer.parseInt(params.get("keyid").toString()));
                    returnnum = pstmt.executeUpdate();
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (type.trim().equals("del")) {
                sql = "delete from  " + serverTable + "  where id=?";
                PreparedStatement pstmt = db.getPstmt(conn, sql);
                try {

                    pstmt.setInt(1, Integer.parseInt(params.get("keyid").toString()));
                    returnnum = pstmt.executeUpdate();
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (type.trim().equals("add")) {
                sql = "insert into " + mapinfoTable + "(mapname,maplabel,mapowerid,ispublic,updatetime)"
                        + "values(?,?,?,?,?)";
                PreparedStatement pstmt = db.getPstmt(conn, sql,
                        Statement.RETURN_GENERATED_KEYS);

                try {
                    pstmt.setString(1, params.get("mapname").toString());
                    pstmt.setString(2, params.get("maplabel").toString());
                    pstmt.setInt(3, Integer.parseInt(params.get("keyid").toString()));
                    pstmt.setBoolean(4, ispublic);
                    pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    pstmt.executeUpdate();
                    ResultSet rskey = pstmt.getGeneratedKeys();
                    if (rskey.next()) {
                        returnnum = rskey.getInt(1);
                    } else {
                        log.debug("no key!");
                    }

                } catch (SQLException e) {
                    log.debug(e.getMessage());
                    db.closeConection(pstmt, null, conn);
                }
            }
        }
        else if (treelevel == 1) {
            if (type.trim().equals("update")) {
                sql = "update " + mapinfoTable + " set mapname=? ,maplabel=?,updatetime=?,ispublic=? where id=?";
                PreparedStatement pstmt = db.getPstmt(conn, sql);
                try {
                    pstmt.setString(1, params.get("text").toString());
                    pstmt.setString(2, params.get("maplabel").toString());
                    pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    pstmt.setBoolean(4, Boolean.parseBoolean(params.get("ispublic").toString()));
                    pstmt.setInt(5, Integer.parseInt(params.get("keyid").toString()));
                    returnnum = pstmt.executeUpdate();
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (type.trim().equals("del")) {
                sql = "delete from  " + mapinfoTable + "  where id=?";
                PreparedStatement pstmt = db.getPstmt(conn, sql);
                try {
                    
                    pstmt.setInt(1, Integer.parseInt(params.get("keyid").toString()));
                    returnnum = pstmt.executeUpdate();
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (type.trim().equals("add")) {
                sql = "insert into " + layerTable + "(layername,layerlabel,minlevel,maxlevel,mapid,updatetime)"
                        + "values(?,?,?,?,?,?)";
                PreparedStatement pstmt = db.getPstmt(conn, sql,
                        Statement.RETURN_GENERATED_KEYS);

                try {
                    pstmt.setString(1, params.get("layername").toString());
                    pstmt.setString(2, params.get("layerlabel").toString());
                    pstmt.setInt(3, Integer.parseInt(params.get("minlevel").toString()));
                    pstmt.setInt(4, Integer.parseInt(params.get("maxlevel").toString()));
                     pstmt.setInt(5, Integer.parseInt(params.get("keyid").toString()));
                    pstmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                    pstmt.executeUpdate();
                    ResultSet rskey = pstmt.getGeneratedKeys();
                    if (rskey.next()) {
                        returnnum = rskey.getInt(1);
                    } else {
                        log.debug("no key!");
                    }

                } catch (SQLException e) {
                    log.debug(e.getMessage());
                    db.closeConection(pstmt, null, conn);
                }
            }
        }
        else if (treelevel == 2) {
            if (type.trim().equals("update")) {
                sql = "update " + layerTable + " set layername=? ,layerlabel=?,minlevel=?,maxlevel=?,updatetime=? where id=?";
                PreparedStatement pstmt = db.getPstmt(conn, sql);
                try {
                    pstmt.setString(1, params.get("text").toString());
                    pstmt.setString(2, params.get("layerlabel").toString());
                     pstmt.setInt(3, Integer.parseInt(params.get("minlevel").toString()));
                     pstmt.setInt(4, Integer.parseInt(params.get("maxlevel").toString()));
                    pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    pstmt.setInt(6, Integer.parseInt(params.get("keyid").toString()));
                    returnnum = pstmt.executeUpdate();
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }  else if (type.trim().equals("del")) {
                sql="delete from "+layerTable +"  where id=?";
                PreparedStatement pstmt = db.getPstmt(conn, sql);
                /**
                sql = "delete from  " + layerTable + " a,"+levelTable+" b,"+cacheTable+" c  where a.id=b.layerid "
                        + " and b.id=c.layerlevelid  and a.id=?";
                log.debug(sql);**/
                //PreparedStatement pstmt = db.getPstmt(conn, sql);
                try {
                    
                    pstmt.setInt(1, Integer.parseInt(params.get("keyid").toString()));
                    returnnum = pstmt.executeUpdate();
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
         return returnnum;
    }

    public int MapinfoToDb(Map<String, Object> params) {

        PostgreSql db = new PostgreSql();
        conn = getConn();

        String check_sql = "select id from " + mapinfoTable + " where mapower=? and maplabel=?";
        PreparedStatement check_pstmt = db.getPstmt(conn, check_sql);
        try {
            check_pstmt.setString(1, params.get("mapower").toString());
            check_pstmt.setString(2, params.get("maplabel").toString());

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(check_pstmt, null, conn);
        }
        int check_id = db.ExistId(check_pstmt);
        if (check_id > 0) {
            return check_id;
        }

        String sql = "insert into " + mapinfoTable + "(mapname,maplabel,mapower,spatialreference,projection,ispublic,updatetime)"
                + "values(?,?,?,?,?,?,?)";
        PreparedStatement pstmt = db.getPstmt(conn, sql,
                Statement.RETURN_GENERATED_KEYS);

        int key = keydefault;
        try {
            pstmt.setString(1, params.get("mapname").toString());
            pstmt.setString(2, params.get("maplabel").toString());
            pstmt.setString(3, params.get("mapower").toString());
            pstmt.setString(4, params.get("spatialreference").toString());
            pstmt.setString(5, params.get("projection").toString());
            pstmt.setBoolean(6, ispublic);
            pstmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

            pstmt.executeUpdate();
            ResultSet rskey = pstmt.getGeneratedKeys();
            if (rskey.next()) {
                key = rskey.getInt(1);
            } else {
                log.debug("no key!");
            }

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
            return key;
        }
        db.closeConection(pstmt, null, null);
        return key;


    }

    public int makeTask(Map<String, Object> params) {


        PostgreSql db = new PostgreSql();
        conn = getConn();

        String sql = "insert into " + taskTable + "(layerid,layerlevel,maxx,minx,maxy,miny,username,state,bgtm,levelid)"
                + "values(?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = db.getPstmt(conn, sql,
                Statement.RETURN_GENERATED_KEYS);

        int key = keydefault;
        try {
            pstmt.setInt(1, (Integer.parseInt(params.get("layerid").toString())));
            pstmt.setInt(2, (Integer.parseInt( params.get("layerlevel").toString())));
            pstmt.setDouble(3, (Double.parseDouble(params.get("maxx").toString())));
            pstmt.setDouble(4, (Double.parseDouble( params.get("minx").toString())));
            pstmt.setDouble(5, (Double.parseDouble(params.get("maxy").toString())));
            pstmt.setDouble(6, (Double.parseDouble(params.get("miny").toString())));
            pstmt.setString(7, params.get("username").toString());
            pstmt.setInt(8, TaskState.init.getState());
            pstmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(10, (Integer.parseInt( params.get("layerlevelid").toString())));

            pstmt.executeUpdate();
            ResultSet rskey = pstmt.getGeneratedKeys();
            if (rskey.next()) {
                key = rskey.getInt(1);
            } else {
                log.debug("no key!");
            }

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
            return key;
        }
        db.closeConection(pstmt, null, null);
        return key;





    }

    
    public void makeDownImg(int id,byte[] img){
        
         PostgreSql db = new PostgreSql();
        conn = getConn();
        
            ImplToPg imgObj = new ImplToPg(); 
            boolean issucss=img==null?false:true;
            String sql = "update " + downTable + " set imgfile=?,updatetime=?,issucess=?,status=?  where id=?";
            //log.debug(sql);
                PreparedStatement pstmt = db.getPstmt(conn, sql);
                try {
                    pstmt.setBytes(1, img);
                    pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    pstmt.setBoolean(3, issucss);
                    pstmt.setInt(4, TaskState.end.getState());
                    pstmt.setInt(5, id);
                    pstmt.executeUpdate();
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
                }

    
    
    }
    
    public int downTilesPrepare(Map<String,Object>params){
        
           PostgreSql db = new PostgreSql();
        conn = getConn();
          int layerlevel = (Integer.parseInt( params.get("layerlevel").toString()));
        double minx = (Double.parseDouble(params.get("minx").toString()));
        double maxy = (Double.parseDouble(params.get("maxy").toString()));
        double maxx = (Double.parseDouble(params.get("maxx").toString()));
        double miny = (Double.parseDouble(params.get("miny").toString()));
        String username=params.get("username").toString();
        int layerid= (Integer.parseInt( params.get("maplayer").toString()));

        String sql = "insert into " + downTable + "(layerid,layerlevel,username,minx,miny,maxx,maxy,status,issucess,updatetime)"
                + "values(?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = db.getPstmt(conn, sql,
                Statement.RETURN_GENERATED_KEYS);
       

        int key = Dbtiandtu.keydefault;
        try {
            pstmt.setInt(1, layerid);
            pstmt.setInt(2, layerlevel);
            pstmt.setString(3, username);
            pstmt.setDouble(4, minx);
            pstmt.setDouble(5, miny);
            pstmt.setDouble(6, maxx);
            pstmt.setDouble(7, maxy);
              pstmt.setInt(8, TaskState.init.getState());
            pstmt.setBoolean(9, false);
            pstmt.setTimestamp(10, new Timestamp(System.currentTimeMillis()));

            pstmt.executeUpdate();
            ResultSet rskey = pstmt.getGeneratedKeys();
            if (rskey.next()) {
                key = rskey.getInt(1);
            } else {
                log.debug("no key!");
            }

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
            return key;
        }
        db.closeConection(pstmt, null, null);
        return key;
    
        
    
    }
    
    public int mapCachePrepare(Map<String, Object> params){
        
          PostgreSql db = new PostgreSql();
        conn = getConn();

        String sql = "insert into " + cacheTable + "(layerlevelid,x,y,ltlon,ltlat,rblon,rblat,updatetime,issucess,taskid)"
                + "values(?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = db.getPstmt(conn, sql,
                Statement.RETURN_GENERATED_KEYS);

        int key = Dbtiandtu.keydefault;
        int x = Integer.parseInt(params.get("x").toString());
        int y = Integer.parseInt(params.get("y").toString());
        int layerlevelid = (Integer.parseInt(params.get("layerlevelid").toString()));
        int taskid = (Integer.parseInt(params.get("taskid").toString()));
       // boolean  issucess=Boolean.parseBoolean(params.get("issucess").toString());

        double ltlon = (Double.parseDouble(params.get("tileminx").toString()));
        double ltlat = (Double.parseDouble(params.get("tilemaxy").toString()));
        double rblon = (Double.parseDouble(params.get("tilemaxx").toString()));
        double rblat = (Double.parseDouble(params.get("tileminy").toString()));

        try {
            pstmt.setInt(1, layerlevelid);
            pstmt.setInt(2, x);
            pstmt.setInt(3, y);
            pstmt.setDouble(4, ltlon);
            pstmt.setDouble(5, ltlat);
            pstmt.setDouble(6, rblon);
            pstmt.setDouble(7, rblat);
            pstmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            pstmt.setBoolean(9, false);
            pstmt.setInt(10, taskid);

            pstmt.executeUpdate();
            ResultSet rskey = pstmt.getGeneratedKeys();
            if (rskey.next()) {
                key = rskey.getInt(1);
            } else {
                log.debug("no key!");
            }

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
            return key;
        }
        db.closeConection(pstmt, null, null);
        return key;
    
    }
    public int mapCache(Map<String, Object> params, byte[] imgbyte) {


        PostgreSql db = new PostgreSql();
        conn = getConn();

        String sql = "insert into " + cacheTable + "(layerlevelid,x,y,ltlon,ltlat,rblon,rblat,img,updatetime,issucess,taskid)"
                + "values(?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = db.getPstmt(conn, sql,
                Statement.RETURN_GENERATED_KEYS);

        int key = Dbtiandtu.keydefault;
        int x = Integer.parseInt(params.get("x").toString());
        int y = Integer.parseInt(params.get("y").toString());
        int layerlevelid = (Integer.parseInt(params.get("layerlevelid").toString()));
        int taskid = (Integer.parseInt(params.get("taskid").toString()));
        boolean  issucess=Boolean.parseBoolean(params.get("issucess").toString());

        double ltlon = (Double.parseDouble(params.get("tileminx").toString()));
        double ltlat = (Double.parseDouble(params.get("tilemaxy").toString()));
        double rblon = (Double.parseDouble(params.get("tilemaxx").toString()));
        double rblat = (Double.parseDouble(params.get("tileminy").toString()));

        try {
            pstmt.setInt(1, layerlevelid);
            pstmt.setInt(2, x);
            pstmt.setInt(3, y);
            pstmt.setDouble(4, ltlon);
            pstmt.setDouble(5, ltlat);
            pstmt.setDouble(6, rblon);
            pstmt.setDouble(7, rblat);
            pstmt.setBytes(8, imgbyte);
            pstmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            pstmt.setBoolean(10, issucess);
            pstmt.setInt(11, taskid);

            pstmt.executeUpdate();
            ResultSet rskey = pstmt.getGeneratedKeys();
            if (rskey.next()) {
                key = rskey.getInt(1);
            } else {
                log.debug("no key!");
            }

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
            return key;
        }
        db.closeConection(pstmt, null, null);
        return key;
    }

    public int layerLevel(Map<String, Object> params) {
        /**
         * layerid int,-- updatetime timestamp,-- layerlevel
         * int,-- resolution float8,-- scale float8--
         */
        PostgreSql db = new PostgreSql();
        conn = getConn();

        int layerlevel = Integer.parseInt( params.get("layerlevel").toString());
        int layerid=Integer.parseInt(params.get("layerid").toString());
        String check_sql = "select id from " + levelTable + " where layerlevel=? and layerid=?";
        PreparedStatement check_pstmt = db.getPstmt(conn, check_sql);
        try {
            check_pstmt.setInt(1, layerlevel);
            check_pstmt.setInt(2, layerid);

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(check_pstmt, null, conn);
        }
        int check_id = db.ExistId(check_pstmt);
        if (check_id > 0) {
            return check_id;
        }




        String sql = "insert into " + levelTable + "(layerid,layerlevel,resolution,scale,updatetime)"
                + "values(?,?,?,?,?)";
        PreparedStatement pstmt = db.getPstmt(conn, sql,
                Statement.RETURN_GENERATED_KEYS);

        int key = Dbtiandtu.keydefault;
        Config scaleconfig = Config.getConfig("config.properties");
        String[] scales = scaleconfig.getValue("tiandituresolution").split(",");
        String[] scale_resolution = scales[layerlevel - 1].split(":");
        double resolution = Double.parseDouble(scale_resolution[0]);
        double scale = Double.parseDouble(scale_resolution[1]);
        try {
            pstmt.setInt(1, layerid);
            pstmt.setInt(2, layerlevel);
            pstmt.setDouble(3, resolution);
            pstmt.setDouble(4, scale);
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

            pstmt.executeUpdate();
            ResultSet rskey = pstmt.getGeneratedKeys();
            if (rskey.next()) {
                key = rskey.getInt(1);
            } else {
                log.debug("no key!");
            }

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
            return key;
        }
        db.closeConection(pstmt, null, null);
        return key;



    }

    
    public void updateDownState(int state,int id){
     PostgreSql db = new PostgreSql();
        conn = getConn();

        String check_sql = "update " + downTable + " set status=?, updatetime=? where id=?";
        PreparedStatement check_pstmt = db.getPstmt(conn, check_sql);
        try {
            check_pstmt.setInt(1, state);
            check_pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            check_pstmt.setInt(3, id);

            check_pstmt.executeUpdate();

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(check_pstmt, null, conn);
        }



    
    }
    
    public void updateTaskState(int state, int taskid) {
        PostgreSql db = new PostgreSql();
        conn = getConn();

        String check_sql = "update " + taskTable + " set state=?, edtm=? where id=?";
        PreparedStatement check_pstmt = db.getPstmt(conn, check_sql);
        try {
            check_pstmt.setInt(1, state);
            check_pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            check_pstmt.setInt(3, taskid);

            check_pstmt.executeUpdate();

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(check_pstmt, null, conn);
        }



    }

    public int layerType(Map<String, Object> params) {

        PostgreSql db = new PostgreSql();
        conn = getConn();

        String check_sql = "select id from " + layerTable + " where layerlabel=? and mapid=?";
        PreparedStatement check_pstmt = db.getPstmt(conn, check_sql);
        try {
            check_pstmt.setString(1, params.get("layerlabel").toString());
            check_pstmt.setInt(2, (Integer) params.get("mapid"));

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(check_pstmt, null, conn);
        }
        int check_id = db.ExistId(check_pstmt);
        if (check_id > 0) {
            return check_id;
        }



        String sql = "insert into " + layerTable + "(mapid,layername,layerlabel,updatetime)"
                + "values(?,?,?,?)";
        PreparedStatement pstmt = db.getPstmt(conn, sql,
                Statement.RETURN_GENERATED_KEYS);

        int key = Dbtiandtu.keydefault;
        try {
            pstmt.setInt(1, (Integer) params.get("mapid"));
            pstmt.setString(2, params.get("layername").toString());
            pstmt.setString(3, params.get("layerlabel").toString());
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            pstmt.executeUpdate();
            ResultSet rskey = pstmt.getGeneratedKeys();
            if (rskey.next()) {
                key = rskey.getInt(1);
            } else {
                log.debug("no key!");
            }

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
            return key;
        }
        db.closeConection(pstmt, null, null);
        return key;

    }

    public Map<String, Object> mapHistory(int levelid) {
        Map<String, Object> historyObj = new HashMap<String, Object>();
        historyObj.put("isLevel", false);
        PostgreSql db = new PostgreSql();
        conn = getConn();

        String check_sql = "select id,maxx,minx,maxy,miny,edtm from " + taskTable + " where levelid=? and state not in (1,-1)";
        PreparedStatement check_pstmt = db.getPstmt(conn, check_sql);
        try {
            check_pstmt.setInt(1, levelid);
            ResultSet rs = check_pstmt.executeQuery();
            ArrayList<Map<String, Object>> bounds_array = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                Map<String, Object> bounds_obj = new HashMap<String, Object>();

                //1000*60*60*24
                if ((new Timestamp(System.currentTimeMillis()).getTime() - rs.getTimestamp(6).getTime())
                        < Long.parseLong(Config.getConfig("config.properties").getValue("mapupdateTime").toString()) * oneyear) {
                    bounds_obj.put("maxx", rs.getDouble(2));
                }
                bounds_obj.put("minx", rs.getDouble(3));
                bounds_obj.put("maxy", rs.getDouble(4));
                bounds_obj.put("miny", rs.getDouble(5));
                //bounds_obj.put("edtn", rs.getTimestamp(6));
                bounds_array.add(bounds_obj);
            }
            historyObj.put("bounds_array", bounds_array);
            if (!bounds_array.isEmpty()) {
                historyObj.put("isLevel", true);
            }


        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(check_pstmt, null, conn);
        }

        return historyObj;

    }

    private void saveCache(int layerid,int  level,int x,int y,byte[] imgbyte){
         String path=Config.getConfig("config.properties").getValue("cahcedir")+layerid+File.separator+level+File.separator+x+File.separator+y+".png";
         FileFunc.saveFilefromBytes(path, imgbyte);
         
    }
    
    
    private void delMultiData(int id) {
        PostgreSql db = new PostgreSql();
        conn = getConn();

        String del_sql = "delete from " + cacheTable + " where id=?";
        PreparedStatement del_pstmt = db.getPstmt(conn, del_sql);
        try {
            del_pstmt.setInt(1, id);

            del_pstmt.executeUpdate();

        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(del_pstmt, null, conn);
        }

    }
    
     private void refinishData(int cacheid,String imgurl) {
        PostgreSql db = new PostgreSql();
        conn = getConn();
        
            ImplToPg imgObj = new ImplToPg(); 
            byte[] imgBytes = imgObj.readUrlmap(imgurl);
            boolean issucss=imgBytes==null?false:true;
            String sql = "update " + cacheTable + " set img=?,updatetime=?,issucess=?  where id=?";
                PreparedStatement pstmt = db.getPstmt(conn, sql);
                try {
                    pstmt.setBytes(1, imgBytes);
                    pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    pstmt.setBoolean(3, issucss);
                    pstmt.setInt(4, cacheid);
                    pstmt.executeUpdate();
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
                     db.closeConection(pstmt, null, conn);
                }
                
                
        String select_sql="select a.x,a.y,b.layerlevel,c.id from "+cacheTable+"  a,"+levelTable+"  b ,"+
        mapinfoTable+"  c,"+layerTable+" d  where  c.id=d.mapid and"
        + "  d.id=b.layerid and b.id=a.layerlevelid and a.id=?";

        log.debug(select_sql);

          PreparedStatement pstmt1 = db.getPstmt(conn, select_sql);
         try {
                pstmt1.setInt(1, cacheid);
               ResultSet rs = pstmt1.executeQuery();
                while (rs.next()) {
                    int maplayerid=rs.getInt("id");
                    int level=rs.getInt("layerlevel");
                    int x=rs.getInt("x");
                    int y=rs.getInt("y");
                    SaveCacheThread saveT=new SaveCacheThread(maplayerid,level,x,y,imgBytes);
                    Thread t1 = new Thread(saveT);
                        t1.start();
                //mapower=rs.getString(1);
                }


        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt1, null, conn);
        }

    }

    public String readmapOwer(int mapid){
         PostgreSql db = new PostgreSql();
        conn = getConn();
        String sql= "select a.mapower from  " + mapinfoTable+  " a,"+serverTable+" b"+ " where a.mapowerid=b.id  and  a.id=?";
         PreparedStatement pstmt = db.getPstmt(conn, sql);
         String mapower="";
         try {
            pstmt.setInt(1, mapid);
               ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                mapower=rs.getString(1);
                }


        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
        }
    return mapower;
    }
    
    private ArrayList <Map<String, Object>> readlayerdataList(int layerid){
        
        PostgreSql db = new PostgreSql();
        conn = getConn();
    
                    ArrayList<Map<String, Object>> leaflist = new ArrayList<Map<String, Object>>();
         String sql_leaf = "select id, layername,minlevel,maxlevel,layerlabel from " + layerTable + "  where mapid=?";
                    PreparedStatement pstmt_leaf = db.getPstmt(conn, sql_leaf);
                            try {
                    pstmt_leaf.setInt(1, layerid);
                    ResultSet rs_leaf = pstmt_leaf.executeQuery();
                        while (rs_leaf.next()) {

                            Map<String, Object> leaf_node = new HashMap<String, Object>();
                            leaf_node.put("text", rs_leaf.getString(2));
                            
                            leaf_node.put("layerdataid", rs_leaf.getInt(1));
                            leaf_node.put("minlevel", rs_leaf.getInt(3));
                            leaf_node.put("maxlevel", rs_leaf.getInt(4));
                            leaf_node.put("layerlabel", rs_leaf.getString(5));
                            leaflist.add(leaf_node);

                        
                    }
                            }catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt_leaf, null, conn);
        }
                            return leaflist ;
    
    }
    public ArrayList<Map<String, Object>> readlayerList(String mapower,boolean isall,boolean ispublic) {
        
            PostgreSql db = new PostgreSql();
        conn = getConn();
    
                    ArrayList<Map<String, Object>> leaflist = new ArrayList<Map<String, Object>>();
         String sql_leaf = "select a.mapname, b.mapower,a.maplabel,a.id,a.updatetime,a.ispublic from " + mapinfoTable 
                 +" a,"+serverTable+" b "+"  where a.mapowerid=b.id and b.mapower=?  ";
         if(ispublic) {
            sql_leaf+="and a.ispublic=true";
        }
         
                    PreparedStatement pstmt_leaf = db.getPstmt(conn, sql_leaf);
                            try {
                    pstmt_leaf.setString(1, mapower);


                    ResultSet rs_leaf = pstmt_leaf.executeQuery();
                        while (rs_leaf.next()) {

                            Map<String, Object> leaf_node = new HashMap<String, Object>();
                            leaf_node.put("text", rs_leaf.getString(1));
                            
                            leaf_node.put("mapower", rs_leaf.getString(2));
                            leaf_node.put("maplabel", rs_leaf.getString(3));
                            leaf_node.put("layer", rs_leaf.getInt(4));
                            leaf_node.put("key", rs_leaf.getInt(4));
                              leaf_node.put("updatetime", rs_leaf.getTimestamp(5).getTime());
                              leaf_node.put("ispublic", rs_leaf.getBoolean(6));
                            
                            leaf_node.put("treelevel",1);
                            if(!isall){
                               // log.debug(isall);
                                leaf_node.put("leaf", true);
                                leaf_node.put("checked", rs_leaf.isFirst());
                            }
                            if(isall){leaf_node.put("children", readalldataList(rs_leaf.getInt(4)));}
                            leaflist.add(leaf_node);

                        }
                    
                            }catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt_leaf, null, conn);
        }
                            return leaflist ;
    } 
    public ArrayList<Map<String, Object>> readalldataList(int mapid) {
        
            PostgreSql db = new PostgreSql();
        conn = getConn();
    
                    ArrayList<Map<String, Object>> leaflist = new ArrayList<Map<String, Object>>();
         String sql_leaf = "select layername, layerlabel,updatetime,minlevel,maxlevel,id from " + layerTable + "  where mapid=?";
                    PreparedStatement pstmt_leaf = db.getPstmt(conn, sql_leaf);
                            try {
                    pstmt_leaf.setInt(1, mapid);


                    ResultSet rs_leaf = pstmt_leaf.executeQuery();
                        while (rs_leaf.next()) {

                            Map<String, Object> leaf_node = new HashMap<String, Object>();
                            leaf_node.put("text", rs_leaf.getString(1));
                            
                            leaf_node.put("layerlabel", rs_leaf.getString(2));
                            leaf_node.put("updatetime", rs_leaf.getTimestamp(3).getTime());
                            leaf_node.put("minlevel", rs_leaf.getInt(4));
                            leaf_node.put("maxlevel", rs_leaf.getInt(5));
                            leaf_node.put("key", rs_leaf.getInt(6));
                            leaf_node.put("treelevel",2);
                            leaf_node.put("leaf", true);
                            leaflist.add(leaf_node);

                        }
                    
                            }catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt_leaf, null, conn);
        }
                            return leaflist ;
    }
    public int totalCount(String sql){
         PostgreSql db = new PostgreSql();
        conn = getConn();
          PreparedStatement pstmt = db.getPstmt(conn, sql);
          //log.debug(sql);
          
    return db.ExistId(pstmt);
    }
    
    
    public  Map<String, Object>  readdowntask(Map<String, Object> parmas) {
    
        ArrayList<Map<String, Object>> mapltaskist = new ArrayList<Map<String, Object>>();
        Map<String,Object> taskobj=new HashMap();
        
        int limit=Integer.parseInt(parmas.get("limit").toString());
        int start=Integer.parseInt(parmas.get("start").toString());
        String state=parmas.get("state")==null?null:parmas.get("state").toString();
        String username=parmas.get("username")==null?null:parmas.get("username").toString();
        String bgtm=parmas.get("bgtm")==null?null:parmas.get("bgtm").toString();
         String edtm=parmas.get("edtm")==null?null:parmas.get("edtm").toString();
         PostgreSql db = new PostgreSql();
        conn = getConn();
        
        String count_head=" select count(*) from (";
        String count_tail=") as t";
        String sql_head = "select * from ( ";
        String sql_tail=  ") as t limit ? offset ?  ";
        
        String body=" d.mapname,e.mapower,a.layerlevel,a.status,a.id,a.minx,a.maxx,a.miny,a.maxy";
        
            
                body+= " ,a.updatetime  from ";
        String details=downTable
                +" a,"+levelTable+" b ,"+mapinfoTable+" d,"+serverTable+" e  where "
                + "d.id=a.layerid and b.id=a.layerid and e.id=d.mapowerid ";
        
        
        
        if(state!=null){
            details+= " and a.state in("+state+")";
        }
        if(username!=null&&!username.equals("")){
            
            details+= " and a.username='"+username+"'";
        }
        if(bgtm!=null&&!bgtm.equals("")){
            
            details+= " and a.bgtm>=to_timestamp('"+bgtm+"','YYYY-MM-DD HH24:MI:SS')";
        }
        if(edtm!=null&&!edtm.equals("")){
            
            details+= " and a.bgtm<=to_timestamp('"+edtm+"','YYYY-MM-DD HH24:MI:SS')";
        }
        
        details+="  order by a.updatetime desc";
        String sql_body="select  "+body+details;
        
       log.debug(sql_body);
        
        PreparedStatement pstmt = db.getPstmt(conn, sql_head+sql_body+sql_tail);
        
        
                           try {
                    pstmt.setInt(1,limit);
                    
                    pstmt.setInt(2,start);

                    ResultSet rs_leaf = pstmt.executeQuery();
                        while (rs_leaf.next()) {

                            Map<String, Object> leaf_node = new HashMap<String, Object>();
                            leaf_node.put("mapname", rs_leaf.getString(1));
                            
                            leaf_node.put("mapower", rs_leaf.getString(2));
                            leaf_node.put("layerlevel", rs_leaf.getInt(3));
                            leaf_node.put("status", rs_leaf.getInt(4));
                            leaf_node.put("updatetime", rs_leaf.getTimestamp(10).getTime());
                            leaf_node.put("taskid", rs_leaf.getInt(5));
                            leaf_node.put("minx", rs_leaf.getDouble(6));
                            leaf_node.put("maxx", rs_leaf.getDouble(7));
                            leaf_node.put("miny", rs_leaf.getDouble(8));
                            leaf_node.put("maxy", rs_leaf.getDouble(9));
                            
                           
                            mapltaskist.add(leaf_node);
                        }
                        db.closeConection(pstmt, rs_leaf, null);
                    
                            }catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
        }
        taskobj.put("totalCount", totalCount(count_head+sql_body+count_tail));
        
        taskobj.put("results", mapltaskist);
        
        return taskobj;
        
    
    }


    public  Map<String, Object>  readmaptask(Map<String, Object> parmas) {
    
        ArrayList<Map<String, Object>> mapltaskist = new ArrayList<Map<String, Object>>();
        Map<String,Object> taskobj=new HashMap();
        
        int limit=Integer.parseInt(parmas.get("limit").toString());
        int start=Integer.parseInt(parmas.get("start").toString());
        String state=parmas.get("state")==null?null:parmas.get("state").toString();
        String mapower=parmas.get("mapower")==null?null:parmas.get("mapower").toString();
        String username=parmas.get("username")==null?null:parmas.get("username").toString();
        String bgtm=parmas.get("bgtm")==null?null:parmas.get("bgtm").toString();
         String edtm=parmas.get("edtm")==null?null:parmas.get("edtm").toString();
         PostgreSql db = new PostgreSql();
        conn = getConn();
        
        String count_head=" select count(*) from (";
        String count_tail=") as t";
        String sql_head = "select * from ( ";
        String sql_tail=  ") as t limit ? offset ?  ";
        
        String body=" d.mapname,c.layername,b.layerlevel,a.state,a.bgtm,a.id,a.minx,a.maxx,a.miny,a.maxy";
        if(state!=null&&(!state.equals("2"))){ 
            body += ",(select count(*) as failnum from "+cacheTable+"  f where f.issucess=false and a.id=f.taskid), ";
             body +="(select count(*) as totalnum from "+cacheTable+"  f where a.id=f.taskid) ";
        }
            
                body+= " ,a.edtm,e.mapower,c.mapid  from ";
        String details=taskTable
                +" a,"+levelTable+" b ,"+layerTable+" c,"+mapinfoTable+" d,"+serverTable+" e  where "
                + "d.id=c.mapid and c.id=b.layerid and b.id=a.levelid and e.id=d.mapowerid  ";
        
        if(state!=null){
            details+= " and a.state in("+state+")";
        }
        if(username!=null&&!username.equals("")){
            
            details+= " and a.username='"+username+"'";
        }
        if(bgtm!=null&&!bgtm.equals("")){
            
            details+= " and a.bgtm>=to_timestamp('"+bgtm+"','YYYY-MM-DD HH24:MI:SS')";
        }
        if(edtm!=null&&!edtm.equals("")){
            
            details+= " and a.bgtm<=to_timestamp('"+edtm+"','YYYY-MM-DD HH24:MI:SS')";
        }
        if(mapower!=null&&!mapower.equals("")){
          details+= " and e.mapower='"+mapower+"'";
        }
        
        details+="  order by a.bgtm desc";
        String sql_body="select  "+body+details;
        
        
        PreparedStatement pstmt = db.getPstmt(conn, sql_head+sql_body+sql_tail);
        
        
                           try {
                    pstmt.setInt(1,limit);
                    
                    pstmt.setInt(2,start);

                    ResultSet rs_leaf = pstmt.executeQuery();
                        while (rs_leaf.next()) {

                            Map<String, Object> leaf_node = new HashMap<String, Object>();
                            leaf_node.put("mapname", rs_leaf.getString(1));
                                  leaf_node.put("mapower", rs_leaf.getString("mapower"));
                                    leaf_node.put("mapid", rs_leaf.getInt("mapid"));
                            leaf_node.put("layername", rs_leaf.getString(2));
                            leaf_node.put("layerlevel", rs_leaf.getInt(3));
                            leaf_node.put("state", rs_leaf.getInt(4));
                            leaf_node.put("bgtm", rs_leaf.getTimestamp(5).getTime());
                            leaf_node.put("taskid", rs_leaf.getInt(6));
                            leaf_node.put("minx", rs_leaf.getDouble(7));
                            leaf_node.put("maxx", rs_leaf.getDouble(8));
                            leaf_node.put("miny", rs_leaf.getDouble(9));
                            leaf_node.put("maxy", rs_leaf.getDouble(10));
                            
                            if(state!=null&&(!state.equals("2"))){ 
                            leaf_node.put("failnum", rs_leaf.getInt(11));
                            leaf_node.put("totalnum", rs_leaf.getInt(12));
                            leaf_node.put("edtm",rs_leaf.getTimestamp(13)==null?null: rs_leaf.getTimestamp(13).getTime());
        }else{
                                 leaf_node.put("edtm",rs_leaf.getTimestamp(11)==null?null: rs_leaf.getTimestamp(11).getTime());
                            }
                            
                           
                            mapltaskist.add(leaf_node);
                        }
                        db.closeConection(pstmt, rs_leaf, null);
                    
                            }catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
        }
        taskobj.put("totalCount", totalCount(count_head+sql_body+count_tail));
        
        taskobj.put("results", mapltaskist);
        
        return taskobj;
        
    
    }

    
    public ArrayList<Map<String, Object>> readmapTree(Map<String, Object> parmas) {

        boolean ismap=parmas.get("ismap")==null?false:Boolean.parseBoolean(parmas.get("ismap").toString());
        boolean isall=parmas.get("isall")==null?false:Boolean.parseBoolean(parmas.get("isall").toString());
        boolean ispublic=parmas.get("ispublic")==null?false:Boolean.parseBoolean(parmas.get("ispublic").toString());
        String  mapower=parmas.get("mapower")==null?null:parmas.get("mapower").toString();
        String maptype=parmas.get("maptype")==null?null:parmas.get("maptype").toString();
        int layerid=parmas.get("layerdata")==null?0:Integer.parseInt(parmas.get("layerdata").toString());
        
        ArrayList<Map<String, Object>> maplist = new ArrayList<Map<String, Object>>();
        if(mapower!=null){
        maplist=readlayerList(mapower,isall,ispublic);
        }else if(layerid!=0){
            maplist=readlayerdataList(layerid);
        }
        else{
            PostgreSql db = new PostgreSql();
        conn = getConn();
        String sql_folder = "select mapower,updatetime,spatialreference,projection,id,owername,maptype from  " + serverTable ;
        if(maptype!=null) {
                sql_folder+="  where maptype in ("+maptype+")";
            }
        
        sql_folder+= " order by id asc ";
       
        PreparedStatement pstmt = db.getPstmt(conn, sql_folder);
        try {
            ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Map<String, Object> node = new HashMap<String, Object>();
                    mapower = rs.getString(1);
                    node.put("text", rs.getString(6));
                    node.put("name", mapower);
                    
                    node.put("expanded", true);
                    node.put("treelevel", 0);
                    node.put("updatetime", rs.getTimestamp(2)==null?null:rs.getTimestamp(2).getTime());
                    node.put("projection", rs.getString(3));
                   node.put("spatialreference", rs.getString(4));
                   node.put("key", rs.getInt(5));
                   node.put("maptype", rs.getString(7));
                   if(!ismap) {
                       ArrayList<Map<String, Object>> child_List=readlayerList(mapower,isall,ispublic);
                        node.put("children", child_List);
                        node.put("size", child_List.size());
                        
                    }
                    maplist.add(node);
                    //if(!rs.isFirst())rs.deleteRow();
                }
        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
        }
        }

        

        return maplist;
    }

    public String getmaplable(int mapid){
    
     PostgreSql db = new PostgreSql();
        conn = getConn();
        //Statement stmt=db.getStmt(conn);
        String sql = "select a.maplabel from "  + mapinfoTable + " a where a.id=?";
        String result="";
        PreparedStatement pstmt = db.getPstmt(conn, sql);
        try {
                            pstmt.setInt(1, mapid);
            
            
            ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    result=rs.getString(1);
                }


        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
        }

        return result;
        
        
    }
    
    public boolean readdbconect(Map<String, Object> params){
        String url=params.get("url").toString();
        String username=params.get("username").toString();
        String password=params.get("password").toString();
        
        boolean flag=false;
           PostgreSql db = new PostgreSql();
           Connection mycon=db.getConn(url, username, password);
        try {
            if(mycon==null||mycon.isClosed()){
            flag= false;
            }
            else{
            mycon.close();
            flag= true;
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Dbtiandtu.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
        
            return flag;
        
        }
    
    }
    
    public byte[] readTileImg(Map<String, Object> params) {
    
     int imgid= Integer.parseInt(params.get("taskid").toString());
     
       byte[] imgbyte = null;
        PostgreSql db = new PostgreSql();
        conn = getConn();
        //Statement stmt=db.getStmt(conn);
        String sql = "select a.imgfile from " + downTable + " a where a.id=?";
                     
        PreparedStatement pstmt = db.getPstmt(conn, sql);
        try {
                pstmt.setInt(1, imgid);
               
            ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    imgbyte = rs.getBytes("imgfile");
                }


        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
        }

        return imgbyte;
    
    
    
    }

    
    
    public byte[] readCache(int maplayerid,int x,int y,int level){
        
        String path=Config.getConfig("config.properties").getValue("cahcedir")+maplayerid+File.separator+level+File.separator+x+File.separator+y+".png";
        //log.debug(path);
            File file = new File(path);
            if(file.exists()){
            return FileFunc.readFileByBytes(path);
            
            }
            else {
            return null;
            }
    
    }
    
    public byte[] readTile(Map<String, Object> params) {

        //String layerlabel = params.get("T").toString();
       // log.debug("begin");
        
        
        String mapower=params.get("mapower")==null?null:params.get("mapower").toString();
        String maplabel= params.get("maplabel")==null?null:params.get("maplabel").toString();
         int maplayerid= params.get("maplayerid")==null?-1:Integer.parseInt(params.get("maplayerid").toString());
        int level = Integer.parseInt(params.get("L").toString());
        int x = Integer.parseInt(params.get("X").toString());
        int y = Integer.parseInt(params.get("Y").toString());
        byte[] imgbyte = readCache(maplayerid,x,y,level);
        if(imgbyte!=null)return imgbyte;
        PostgreSql db = new PostgreSql();
        conn = getConn();
        //Statement stmt=db.getStmt(conn);
        String sql = "select a.* from " + cacheTable + " a," + levelTable + " b ," + mapinfoTable + " c," + layerTable + " d,  "+serverTable+" e ";
               if(maplayerid>0){
                   sql+= " where c.id=?";
               }else{
                   sql+= " where c.maplabel=?";
               } 
                sql+= " and  c.id=d.mapid and e.id=c.mapowerid and d.id=b.layerid and b.id=a.layerlevelid";
                sql+= " and a.x=? and a.y=? and b.layerlevel=? and";
                if(maplayerid<0){
                  sql  += " e.mapower=? and ";
                        
               }
                sql+= " a.issucess=true order by a.id desc";
                     
        PreparedStatement pstmt = db.getPstmt(conn, sql);
        try {
             if(maplayerid>0){
                pstmt.setInt(1, maplayerid);
               }else{
                   pstmt.setString(1, maplabel);
               } 
            
            pstmt.setInt(2, x);
            pstmt.setInt(3, y);
            pstmt.setInt(4, level);
             if(maplayerid<0){
                 pstmt.setString(5, mapower);
               }
            
            ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    if (!rs.isFirst()) {
                        DelCacheThread delT = new DelCacheThread(rs.getInt("id"));
                        Thread t = new Thread(delT);
                        t.start();
                        continue;
                    }
                    imgbyte = rs.getBytes("img");
                    
                    SaveCacheThread saveT=new SaveCacheThread(maplayerid,level,x,y,imgbyte);
                    Thread t1 = new Thread(saveT);
                        t1.start();
                    
                    //if(!rs.isFirst())rs.deleteRow();
                }


        } catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(pstmt, null, conn);
        }
        //log.debug("end");
        return imgbyte;
    }

    class DelCacheThread implements Runnable {

        private int cacheid = 0;

        public void run() {
            Dbtiandtu tddao = new Dbtiandtu();
            tddao.delMultiData(cacheid);
        }

        public DelCacheThread(int id) {

            cacheid = id;
        }
    }
    
     class SaveCacheThread implements Runnable {

        private int layerid = 0;
         private int level = 0;
         private int x = 0;
         private int y = 0;
         private byte[] img=null;

        public void run() {
            Dbtiandtu tddao = new Dbtiandtu();
            tddao.saveCache(layerid, level, x, y, img);
        }

        public SaveCacheThread(int layerid_p,int level_p,int x_p,int y_p,byte[]img_p) {

            layerid = layerid_p;
            level=level_p;
            x=x_p;
            y=y_p;
            img=img_p;
        }
    }
    
    class RefinishCacheThread implements Runnable {

        private int cacheid = 0;
        private String img_url=null;

        public void run() {
            Dbtiandtu tddao = new Dbtiandtu();
            tddao.refinishData(cacheid,img_url);
        }

        public RefinishCacheThread(int id,String img) {

            cacheid = id;
            img_url=img;
        }
    }
    
     class finishCacheThread implements Runnable {

        private ArrayList<Long> id_list = new ArrayList();
         private  long mytaskid=0;

        

        public void run() {
            
           
            
            PostgreSql db = new PostgreSql();
        conn = getConn();
            Dbtiandtu tddao = new Dbtiandtu();
            /*String limits="";
            for (Long long1 : id_list) {
                limits+=","+long1;
            }
            limits=limits.substring(1);*/
            long min=id_list.get(0);
            long max=id_list.get(1);
            
/*
            String check_sql = "select b.*,c.layerlabel,d.layerlevel,f.mapower from " + taskTable + " a,"+cacheTable
                    +" b,"+layerTable+" "+ " c,"+mapinfoTable+" e,"+ serverTable+" f,"
                    +levelTable+" d  where b.taskid=a.id and a.levelid=d.id and "
                + "d. layerid=c.id and c.mapid=e.id and e.mapowerid=f.id and b.issucess=false and b.id in ("+limits +") order by b.id";
*/

            String check_sql = "select b.*,c.layerlabel,d.layerlevel,f.mapower from " + taskTable + " a,"+cacheTable
                    +" b,"+layerTable+" "+ " c,"+mapinfoTable+" e,"+ serverTable+" f,"
                    +levelTable+" d  where b.taskid=a.id and a.levelid=d.id and "
                    + "d. layerid=c.id and c.mapid=e.id and e.mapowerid=f.id and b.issucess=false and b.id >=? and b.id<=? and a.id=? order by b.id";

            // log.debug(check_sql);
            
        PreparedStatement check_pstmt = db.getPstmt(conn, check_sql);
        try {
            check_pstmt.setLong(1,min);
            check_pstmt.setLong(2,max);
            check_pstmt.setLong(3,mytaskid);
            log.debug(min);
            log.debug(max);
            log.debug(mytaskid);
            ResultSet rs = check_pstmt.executeQuery();
              Config scaleconfig = Config.getConfig("config.properties");
              List<Thread> list = new ArrayList<Thread>();
            while (rs.next()&&(!Thread.currentThread().isInterrupted())) {
                
             
              String layerlabel=rs.getString("layerlabel");
              int x_num=rs.getInt("x");
              int y_num=rs.getInt("y");
              int layerlevel=rs.getInt("layerlevel");
              int cacheid=rs.getInt("id");
              String mapower=rs.getString("mapower");
              //log.debug(rs.getString("mapower"));
              Map<String,Object> arguments = new HashMap<String, Object>();
              arguments.put("x", x_num);
             arguments.put("y", y_num);
             arguments.put("z", layerlevel);
              
              String imgUrl=StringHelper.urlformat(Imptianditu.getRandomServer(layerlabel), arguments);
              /**
                switch (mapower) {
                    case "tianditu":
                        imgUrl = Imptianditu.getRandomServer(scaleconfig,"tiandituserver") + "?T=" 
                                + layerlabel + "&X=" + x_num + "&Y=" + y_num + "&L=" + layerlevel;
                        break;
                    case "google":
                        String[]layerlabels=layerlabel.split(":");
                        imgUrl = "http://"+layerlabels[0]+Imptianditu.getRandomServer(scaleconfig,"googleserver") +"/"
                                +layerlabels[1]+ "?" + layerlabels[2] + "&x=" + x_num + "&y=" + y_num + "&z=" + layerlevel+"&hl=zh-CN";
                        break;
                }
              **/
              log.debug("抓取图片地址:"+imgUrl);
                tddao.refinishData(cacheid,imgUrl);
              
            }} catch (SQLException e) {
            log.debug(e.getMessage());
            db.closeConection(check_pstmt, null, conn);
        }
            
            
            
        }

        public finishCacheThread(ArrayList<Long> list,long taskid) {

            id_list =list;
            mytaskid=taskid;
        }
    }
    

    public static void main(String[] args) {
        log.debug(new Timestamp(System.currentTimeMillis()));

    }
}
