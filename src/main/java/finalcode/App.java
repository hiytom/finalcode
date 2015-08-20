package finalcode;


import finalcode.HttpAsyncClient.HttpPoolRequest;
import finalcode.OperateData.ConcurrentData;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main App
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static final String baseUrl;

    static {
        // 加载 log 配置文件
        String basePath = App.class.getResource("/").getPath();
        PropertyConfigurator.configure(basePath + "log4j.properties");

        baseUrl = "http://www.klook.com";
        ConcurrentData.URL.offer(basePath);
    }

    public static void main(String[] args) {
        HttpPoolRequest.newHttpRequest();
    }

}
