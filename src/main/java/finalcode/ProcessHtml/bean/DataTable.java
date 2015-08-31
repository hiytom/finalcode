package finalcode.processHtml.bean;

import java.util.Date;

/**
 * Created by peng_chao on 15-8-28.
 */
public class DataTable {
    public int id;
    public String link; // url
    public String companyName; // 公司名称
    public String workPlace; // 工作地点
    public int lowestExperience; // 最低工作时间
    public int tiptopExperience; // 最高工作时间
    public String educational; // 教育程度
    public String wayOfWork; // 工作方式
    public String profession; // 行业
    public String releaseDate; // 发布时间
    public String financing; // 融资
    public int lowestWage; // 最低薪资
    public int tiptopWage; // 最高薪资
    public String welfare; // 福利
    public String language; // 语言
    public String skill; // 技能
    public String html; // 页面源码
    public String size; // 规模

    @Override
    public String toString() {
        return "DataTable{" +
                "id=" + id +
                ", link='" + link + '\'' +
                ", companyName='" + companyName + '\'' +
                ", workPlace='" + workPlace + '\'' +
                ", lowestExperience=" + lowestExperience +
                ", tiptopExperience=" + tiptopExperience +
                ", educational='" + educational + '\'' +
                ", wayOfWork='" + wayOfWork + '\'' +
                ", profession='" + profession + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", financing='" + financing + '\'' +
                ", lowestWage=" + lowestWage +
                ", tiptopWage=" + tiptopWage +
                ", welfare='" + welfare + '\'' +
                ", language='" + language + '\'' +
                ", skill='" + skill + '\'' +
                ", html='" + html + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
