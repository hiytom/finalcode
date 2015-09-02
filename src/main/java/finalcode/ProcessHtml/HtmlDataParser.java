package finalcode.processhtml;

import finalcode.App;
import finalcode.operatedata.ConcurrentData;
import finalcode.processhtml.bean.DataTable;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by peng_chao on 15-8-19.
 * Html 页面处理
 */
public interface HtmlDataParser {

    static String parse(String html) {
        Document document = Jsoup.parse(html);
        document.select("a[href]").stream()
                .map(link -> StringUtils.deleteWhitespace(link.absUrl("href")))
                .filter(link -> StringUtils.isNotEmpty(link) && link.startsWith(App.baseUrl)
                        && ConcurrentData.REPEAT.add(link))
                .forEach(ConcurrentData.URL::offer);

        return document.select(".finalcodelocation").text();
    }

    DataTable getData(String html);
}
