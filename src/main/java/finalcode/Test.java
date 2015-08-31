package finalcode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by peng_chao on 15-8-28.
 */
public class Test {
    public static void main(String[] args) {
        String a = "经验3-5年";
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(a);
        System.out.println(m.replaceAll(" ").trim());
    }
}
