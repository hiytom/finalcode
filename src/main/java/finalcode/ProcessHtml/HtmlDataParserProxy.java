package finalcode.processhtml;

import finalcode.App;
import finalcode.operatedata.ConcurrentData;
import finalcode.processhtml.bean.DataTable;
import finalcode.utils.FinalCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by peng_chao on 15-8-28.
 */
public class HtmlDataParserProxy implements InvocationHandler {
    private Object target;
    private static boolean URLRegex = App.regex == null;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String html = (String) args[0];
        Object result = null;
        String location = parse(html);

        if (recognize(location)) {
            result = method.invoke(target, args);
            if (null != result) {
                ConcurrentData.DATA.offer((DataTable) result);
            }
        }
        return result;
    }

    public Object bind(Object target) {
        this.target = target;
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), this);
    }

    private boolean recognize(String location) {
        boolean temp = true;
        if (!URLRegex && !FinalCodeUtil.recognize(App.regex, location)) temp = false;
        return temp;
    }

    private String parse(String html) {
        Document document = Jsoup.parse(html);
        document.select("a[href]").stream()
                .map(link -> link.absUrl("href"))
                .filter(link -> URLFilter(link))
                .forEach(ConcurrentData.URL::offer);

        return document.select(".finalcodelocation").text();
    }

    private boolean URLFilter(String link) {
        return StringUtils.isNotEmpty(link)
                && link.startsWith(App.host)
                && ConcurrentData.REPEAT.add(link);
    }

}
