package finalcode;


import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    // 启动初始化参数
    static {
        // 加载 log 配置文件
        String basePath = App.class.getResource("/").getPath();
        PropertyConfigurator.configure(basePath + "log4j.properties");
    }


    public static void main(String[] args) {

    }
}
