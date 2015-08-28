package finalcode.processHtml;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import finalcode.App;
import finalcode.operatedata.ConcurrentData;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
                isHandleUrl(linkTemp, urlList);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlList;
    }

    private static void isHandleUrl(String urlTemp, List<String> urlList) {
        urlTemp = StringUtils.deleteWhitespace(urlTemp.trim());
        if (StringUtils.isNotEmpty(urlTemp) && urlTemp.startsWith(App.baseUrl) && ConcurrentData.REPEAT.add(urlTemp)) {
            urlList.add(encode(urlTemp));
        }
    }

    public static String encode(String url, String unicode) {
        if(Strings.isNullOrEmpty(url)){
            return "";
        }

        if (Strings.isNullOrEmpty(unicode)) {
            unicode = "UTF-8";
        }
        try {
            url = URLEncoder.encode(url, unicode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;

    }

    public static String encode(String url) {
        return encode(url, null);
    }


}
