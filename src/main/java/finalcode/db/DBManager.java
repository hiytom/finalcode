package finalcode.db;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;

import java.sql.*;
import java.util.concurrent.Future;

/**
 * Created by peng_chao on 15-8-28.
 */
public final class DBManager {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DBManager.class);
    private static DBManager dbManager;
    private final static DataSource datasource;

    static {
        PoolProperties p = new PoolProperties();
        p.setUrl("jdbc:mysql://localhost:3306/");
        p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername("root");
        p.setPassword("sees7&chanting");
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setFairQueue(true);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

        datasource = new DataSource();
        datasource.setPoolProperties(p);
    }

    private DBManager() {
    }

    public static Connection getConnection() {

        if (null == dbManager) {
            synchronized (DBManager.class) {
                if (null == dbManager) {
                    dbManager = new DBManager();
                }
            }
        }
        Connection conn = null;
        try {
            Future<Connection> future = datasource.getConnectionAsync();
            conn = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭数据库对象
     *
     * @param conn
     * @param rs
     * @param pstm
     */
    public void close(Connection conn, ResultSet rs, PreparedStatement pstm) {
        try {
            if (rs != null) { // 关闭记录集
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstm != null) { // 关闭声明
                    pstm.close();
                    pstm = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) { // 关闭连接对象
                        conn.close();
                        conn = null;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
