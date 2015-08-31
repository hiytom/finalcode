package finalcode.processHtml.impl;

import finalcode.processHtml.HtmlDataParser;
import finalcode.processHtml.bean.DataTable;
import finalcode.utils.FinalCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by peng_chao on 15-8-28.
 */
public class LagouHtmlDataParserImpl implements HtmlDataParser {

    @Override
    public DataTable getData(String html) {
        //Document doc = Jsoup.parse(html);
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.lagou.com/jobs/876534.html").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataTable dataTable = new DataTable();

        dataTable.language = doc.select(".join_tc_icon").select("h1").attr("title");

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
        dataTable.releaseDate = basic.select("div").text();

        Elements companyInfo = doc.select(".job_company");
        dataTable.companyName = companyInfo.select(".b2").attr("alt");
        dataTable.profession = companyInfo.select("li:contains(领域)").first().childNode(1).outerHtml();
        dataTable.size = companyInfo.select("li:contains(规模)").first().childNode(1).outerHtml();
        dataTable.financing = companyInfo.select("li:contains(目前阶段)").first().childNode(1).outerHtml();

        dataTable.skill = doc.select(".job_bt").text();
        dataTable.html = html;
        //dataTable.link = doc.select(".finalcodelocation").text();


        return dataTable;
    }

    private static HashMap dayChinese = new HashMap<>();

    static {
        dayChinese.put("一天", "");
        dayChinese.put("两天", "");
        dayChinese.put("三天", "");
        dayChinese.put("四天", "");
    }

    private Date getLagouReleaseDate(String str) {
        String tempDate = StringUtils.deleteWhitespace(str).split(":")[1];
        Date date = FinalCodeUtil.isFormatDate(tempDate, "yyyy-MM-dd");
        if (date != null) {
            return date;
        } else {

        }
        return null;
    }

    public static void main(String args[]) {
        LagouHtmlDataParserImpl lagouHtmlDataParser = new LagouHtmlDataParserImpl();
        System.out.println(lagouHtmlDataParser.getData(""));
    }
}
