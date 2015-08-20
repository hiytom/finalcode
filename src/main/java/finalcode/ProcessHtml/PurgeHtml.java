package finalcode.ProcessHtml;

import com.google.common.collect.Lists;
import finalcode.OperateData.ConcurrentData;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by peng_chao_b on 15/8/14.
 */
public class PurgeHtml {
    private static final Logger logger = LoggerFactory.getLogger(PurgeHtml.class);

    public static List<String> parse(String html) {
        List<String> urlTemp = Lists.newArrayList();
        try {
            Document doc = Jsoup.parse(html);
            Elements links = doc.select("a[href]");
            Elements imports = doc.select("link[href]");

            for (Element link : imports) {
                String linkTemp = link.attr("abs:href");
                if (StringUtils.isNotEmpty(StringUtils.deleteWhitespace(linkTemp))) {
                    logger.info(linkTemp);
                    urlTemp.add(linkTemp);
                }
            }

            for (Element link : links) {
                String linkTemp = link.attr("abs:href");
                if (StringUtils.isNotEmpty(StringUtils.deleteWhitespace(linkTemp))) {
                    logger.info(linkTemp);
                    urlTemp.add(linkTemp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlTemp;
    }

}
