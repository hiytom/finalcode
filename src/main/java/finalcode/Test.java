package finalcode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by peng_chao on 15-8-22.
 */
public class Test {

    public static void main(String args[]) {
        try {
            Document doc = Jsoup.connect("https://www.airbnb.com/s/Singapore").get();
            Elements hrefs = doc.select("a[href]");
            hrefs.forEach((href) -> {
                String temp = href.absUrl("href");
                System.out.println(temp);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
