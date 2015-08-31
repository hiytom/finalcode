package finalcode;


import finalcode.operatedata.ConcurrentData;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Main App
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static String baseUrl;
    public static String regex;
    public static String achieve;
    public static String table;

    private App() {
    }

    private void init() {

        // 加载 log 配置文件
        String basePath = App.class.getResource("/").getPath();
        PropertyConfigurator.configure(basePath + "log4j.properties");

        InputStream ins = App.class.getResourceAsStream("/initParam.properties");
        Properties prop = new Properties();

        try {
            prop.load(ins);
            regex = prop.getProperty("regex");
            baseUrl = prop.getProperty("baseUrl");
            achieve = prop.getProperty("achieve");
            table = prop.getProperty("table");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConcurrentData.URL.offer(baseUrl);
        ConcurrentData.REPEAT.add(baseUrl);

        // JdbcTemplate.initDataBases();
        FinalCodeFactory.newHttpRequest();

    }

    public static void main(String[] args) {
        App app = new App();
        app.init();
    }

}
