package finalcode;


import finalcode.httpasynclient.HttpPoolRequest;
import finalcode.operatedata.ConcurrentData;
import org.apache.log4j.PropertyConfigurator;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * Main App
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static String baseUrl;
    public static String regex;

    private App() {
    }

    private void init() {
        HttpPoolRequest.newHttpRequest();

        // 加载 log 配置文件
        String basePath = App.class.getResource("/").getPath();
        PropertyConfigurator.configure(basePath + "log4j.properties");

        InputStream ins = App.class.getResourceAsStream("/initParam.properties");
        Properties prop = new Properties();

        try {
            prop.load(ins);
            baseUrl = prop.getProperty("baseUrl");
            regex = prop.getProperty("regex");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConcurrentData.URL.offer(baseUrl);
        ConcurrentData.REPEAT.add(baseUrl);

    }

    public static void main(String[] args) {
        App app = new App();
        app.init();

    }

}
