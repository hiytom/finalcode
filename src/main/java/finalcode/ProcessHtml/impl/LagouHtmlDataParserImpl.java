package finalcode.processhtml.impl;

import finalcode.App;
import finalcode.processhtml.HtmlDataParser;
import finalcode.processhtml.bean.DataTable;
import finalcode.utils.FinalCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by peng_chao on 15-8-28.
 */
public class LagouHtmlDataParserImpl implements HtmlDataParser {
    private static final Logger logger = LoggerFactory.getLogger(LagouHtmlDataParserImpl.class);

    @Override
    public DataTable getData(String html) {
        try {
            Document doc = Jsoup.parse(html);
            if (StringUtils.isEmpty(doc.baseUri())) {
                doc.setBaseUri(App.host);
            }
            DataTable dataTable = new DataTable();
            Elements basic = doc.select(".job_request");
            List<String> list = basic.select("span").stream().map((element) ->
                    StringUtils.deleteWhitespace(element.html())).collect(Collectors.toList());

            if (!list.isEmpty() && list.size() == 5) {
                String[] wage = list.get(0).replaceAll("k", "000").split("-");
                dataTable.lowestWage = Integer.valueOf(wage[0]);
                dataTable.tiptopWage = Integer.valueOf(wage[1]);
                dataTable.workPlace = list.get(1);
                int[] experience = FinalCodeUtil.getNumToStr(list.get(2));
                dataTable.lowestExperience = experience[0];
                dataTable.tiptopExperience = experience[1];
                dataTable.educational = list.get(3);
                dataTable.wayOfWork = list.get(4);
            }

            dataTable.welfare = basic.first().childNode(11).outerHtml().split(":")[1];
            dataTable.releaseDate = getLagouReleaseDate(basic.select("div").text());

            Elements companyInfo = doc.select(".job_company");
            dataTable.companyName = companyInfo.select(".b2").attr("alt");
            dataTable.profession = companyInfo.select("li:contains(领域)").first().childNode(1).outerHtml();
            dataTable.financing = companyInfo.select("li:contains(目前阶段)").first().childNode(1).outerHtml();
            dataTable.language = doc.select(".join_tc_icon").select("h1").attr("title");
            dataTable.skill = doc.select(".job_bt").text();
            dataTable.html = html;
            dataTable.link = doc.select(".finalcodelocation").text();
            System.out.println(dataTable.toString());
            return dataTable;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    private static HashMap<String, Integer> dayChinese = new HashMap<>();

    static {
        dayChinese.put("一天", -1);
        dayChinese.put("两天", -2);
        dayChinese.put("三天", -3);
        dayChinese.put("四天", -4);
        dayChinese.put("五天", -5);
        dayChinese.put("六天", -6);
        dayChinese.put("七天", -7);
    }

    private static Date getLagouReleaseDate(String str) {
        String tempDate = StringUtils.deleteWhitespace(str).split(":")[1];
        final Date[] date = {FinalCodeUtil.isFormatDate(tempDate, "yyyy-MM-dd")};
        if (date[0] == null) {
            dayChinese.forEach((k, v) -> {
                if (tempDate.contains(k)) {
                    date[0] = FinalCodeUtil.getDataBefore(v);
                    return;
                }
            });
        }
        return date[0];
    }
}
