package finalcode;


import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
        try {
            Document doc = Jsoup.connect("http://www.klook.com/zh-CN/").get();
            Elements links = doc.select("a[href]");
            Elements media = doc.select("[src]");
            Elements imports = doc.select("link[href]");

            print("\nMedia: (%d)", media.size());
            for (Element src : media) {
                if (src.tagName().equals("img"))
                    print(" * %s: <%s> %sx%s (%s)",
                            src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                            trim(src.attr("alt"), 20));
                else
                    print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
            }

            print("\nImports: (%d)", imports.size());
            for (Element link : imports) {
                print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
            }

            print("\nLinks: (%d)", links.size());
            for (Element link : links) {
                print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s;
    }


}
