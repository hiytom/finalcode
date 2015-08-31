package finalcode.utils;

import com.google.common.base.Strings;
import finalcode.App;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by peng_chao on 15-8-28.
 */
public final class FinalCodeUtil {

    public static int getProcessors() {
        int a;
        try {
            a = Runtime.getRuntime().availableProcessors();
        } catch (Exception e) {
            a = 4;
        }
        return a;
    }

    public static int[] getNumToStr(String str) {
        int[] a = {};
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(StringUtils.deleteWhitespace(str));
        if (m.find()) {
            String[] temp = m.replaceAll(" ").trim().split(" ");
            a = new int[temp.length];
            for (int i = 0; i < temp.length; i++) {
                a[i] = Integer.valueOf(StringUtils.deleteWhitespace(temp[i]));
            }
        }
        return a;
    }

    public static boolean recognize(String regEx, String url) {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(url);
        if (m.find()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Date isFormatDate(String dataStr, String formatStr) {
        dataStr = StringUtils.deleteWhitespace(dataStr);
        if (StringUtils.isEmpty(dataStr)) {
            return null;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);
            try {
               return dateFormat.parse(dataStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getTableFiledType(Field field) {
        Type type = field.getType();
        String tableFiledType;
        if (type == int.class || type == Integer.TYPE) {
            tableFiledType = "int(11)";
        } else if (type == float.class || type == Float.TYPE) {
            tableFiledType = "float";
        } else if (type == double.class || type == Double.TYPE) {
            tableFiledType = "double";
        } else if (type == String.class) {
            tableFiledType = "text";
        } else if (type == Date.class) {
            tableFiledType = "datetime";
        } else {
            tableFiledType = "varchar(255)";
        }
        return tableFiledType;
    }

    public boolean regexTarget(String url) {
        String regex = App.regex;
        if (StringUtils.isNotEmpty(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher m = pattern.matcher(url);
            if (m.find()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }


    public static String encode(String url, String unicode) {
        if (Strings.isNullOrEmpty(url)) {
            return "";
        }

        if (Strings.isNullOrEmpty(unicode)) {
            unicode = "UTF-8";
        }
        try {
            if (url.length() != url.getBytes().length) {
                url = URLEncoder.encode(url, unicode);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;

    }

    public static String encode(String url) {
        return encode(url, null);
    }
}
