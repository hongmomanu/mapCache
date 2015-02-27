package mapcatche.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import mapcatche.conmmon.Config;

public class PostgreSql {

    private static final Logger log = Logger.getLogger(PostgreSql.class);

    public Connection getConn() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            Config dbconfig = Config.getConfig("config.properties");
            String url = dbconfig.getValue("pgdburl");//"jdbc:postgresql://localhost:5432/postgres";
            String username = dbconfig.getValue("pgdbusername");//"postgres";
            String password = dbconfig.getValue("pgdbpassword");//"1212";//
            log.debug(url);
            log.debug(username);
            log.debug(password);
            try {
                conn = DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return conn;
    }
    
    public Connection getConn(String url,String username,String password) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            try {
                conn = DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally{
        return conn;
        }

        
    }
    

    public Statement getStmt(Connection con) {
        try {
            return con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PreparedStatement getPstmt(Connection con, String sql) {
        try {
            log.debug(sql);
            log.debug(con.isClosed());
            PreparedStatement pstmt = con.prepareStatement(sql);
            return pstmt;
        } catch (SQLException e) {
            log.debug(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public PreparedStatement getPstmt(Connection con, String sql, int returnkey) {
        try {
            PreparedStatement pstmt = con.prepareStatement(sql, returnkey);
            return pstmt;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int ExistId(PreparedStatement pstmt) {
        int existId = 0;
        try {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                existId = rs.getInt(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            log.debug(e.getMessage());
            //e.printStackTrace();
        }
        return existId;

    }

    public void closeConection(Statement stmt, ResultSet rs, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                rs = null;
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                stmt = null;
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                conn = null;
            }

        }


    }
}
