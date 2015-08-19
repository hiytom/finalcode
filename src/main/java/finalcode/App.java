package finalcode;


import finalcode.HttpAsyncClient.HttpClientManager;
import finalcode.ProcessHtml.PurgeHtml;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
        try {
            String html = HttpClientManager.doGet("http://www.klook.com", "utf-8", true);
            PurgeHtml.parse(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
