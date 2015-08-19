package finalcode.ProcessHtml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

/**
 * Created by peng_chao_b on 15/8/14.
 */
public class PurgeHtml {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PurgeHtml.class);

    public static void parse(String html) {
        try {
            Document doc = Jsoup.parse(html);
            Elements links = doc.select("a[href]");
            Elements imports = doc.select("link[href]");

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
