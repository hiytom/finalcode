package finalcode.processhtml;

import finalcode.App;
import finalcode.operatedata.ConcurrentData;
import finalcode.processhtml.bean.DataTable;
import finalcode.utils.FinalCodeUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by peng_chao on 15-8-28.
 */
public class HtmlDataParserProxy implements InvocationHandler {
    private Object target;
    private static boolean URLFilter = App.regex == null;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String html = (String) args[0];
        Object result = null;
        String location = HtmlDataParser.parse(html);

        if (recognize(location)) {
            result = method.invoke(target, args);
            ConcurrentData.DATA.offer((DataTable) result);
        }
        return result;
    }

    public Object bind(Object target) {
        this.target = target;
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), this);
    }

    public boolean recognize(String location) {
        boolean temp = true;
        if (!URLFilter && !FinalCodeUtil.recognize(App.regex, location)) temp = false;
        return temp;
    }


}
