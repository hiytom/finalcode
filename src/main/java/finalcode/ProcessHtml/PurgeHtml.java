package finalcode.processHtml;

import com.google.common.collect.Lists;
import finalcode.App;
import finalcode.operateData.ConcurrentData;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by peng_chao_b on 15/8/14.
 */
public final class PurgeHtml {
    private static final Logger logger = LoggerFactory.getLogger(PurgeHtml.class);

    public static List<String> parse(String html) {
        List<String> urlList = Lists.newArrayList();
        try {
            Document doc = Jsoup.parse(html);
            Elements links = doc.select("a[href]");

            links.forEach((link) -> {
                String linkTemp = link.absUrl("href");
                String urlTemp = StringUtils.deleteWhitespace(linkTemp);
                isHandleUrl(urlTemp, urlList);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlList;
    }

    private static void isHandleUrl(String urlTemp, List<String> urlList) {
        if (StringUtils.isNotEmpty(urlTemp) && urlTemp.startsWith(App.baseUrl) && ConcurrentData.REPEAT.add(urlTemp)) {
            urlList.add(urlTemp);
        }
    }
}
