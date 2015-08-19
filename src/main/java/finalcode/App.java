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
 * Main App
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static final String baseUrl;
    // 启动初始化参数
    static {
        // 加载 log 配置文件
        String basePath = App.class.getResource("/").getPath();
        PropertyConfigurator.configure(basePath + "log4j.properties");
        baseUrl = "http://www.klook.com";
    }

    public static void main(String[] args) {
        try {
            String html = HttpClientManager.doGet(baseUrl, "UTF-8", false);
            PurgeHtml.parse(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
