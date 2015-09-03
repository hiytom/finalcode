package finalcode.processhtml;

import finalcode.processhtml.bean.DataTable;

/**
 * Created by peng_chao on 15-8-19.
 * Html 页面处理
 */
public interface HtmlDataParser {
    DataTable getData(String html);
}
