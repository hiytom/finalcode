package finalcode;


import finalcode.operatedata.ConcurrentData;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Main App
 */
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static String baseUrl;
    public static String host;
    public static String regex;
    public static String achieve;

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        regex = prop.getProperty("regex", null);
        baseUrl = prop.getProperty("baseUrl");
        achieve = prop.getProperty("achieve");

        try {
            URL url = new URL(baseUrl);
            host = url.getProtocol() + "://" + url.getHost();
        } catch (MalformedURLException e) {
            System.exit(0);
        }

        ConcurrentData.URL.offer(baseUrl);
        ConcurrentData.REPEAT.add(baseUrl);

        FinalCodeFactory.newHttpRequest();

    }

    public static void main(String[] args) {
        App app = new App();
        app.init();
    }

}
