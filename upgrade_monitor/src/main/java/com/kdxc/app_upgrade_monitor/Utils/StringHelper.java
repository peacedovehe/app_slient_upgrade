package com.kdxc.app_upgrade_monitor.Utils;

import android.widget.EditText;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xt on 16/9/29.
 */
public class StringHelper {
    public static boolean isEmpty(String str) {
        return str == null || str.equals("null") || str.trim().equals("");
    }

    public static boolean notEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 获取 EditText 的内容
     *
     * @param edt EditText 控件
     * @return String内容
     */
    public static String getEditTextContent(EditText edt) {
        return edt == null ? "" : edt.getText().toString().trim();
    }

    /**
     * 是否 EditText 内容为空
     *
     * @param edt EditText控件
     * @return true 内容为空
     */
    public static boolean isEditTextEmpty(EditText edt) {
        return isEmpty(getEditTextContent(edt));
    }

    /**
     * 获取TextView 的内容
     *
     * @param tv TextView控件
     * @return String内容
     */
    public static String getTextViewContent(TextView tv) {
        return tv == null ? "" : tv.getText().toString().trim();
    }

    /**
     * 判断汉字
     *
     * @param content 内容
     * @return true 是汉字
     */
    public static boolean hasChinese(String content) {
        String regEx = "[\\u4e00-\\u9fa5]+$";   //"^[\\u2E80-\\uFE4F]+$";   //"[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(content);
        return m.find();
    }

    public static boolean checkPwd(String password) {
        String regEx = "^[0-9a-zA-Z]{6,16}$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(password);
        return m.find();
    }

    /**
     * @param str: 需要验证的字符串
     * @return 含有表情返回true
     */
    public static boolean checkExpression(String str) {
        //匹配非表情符号的正则表达式
        String reg = "^([a-z]|[A-Z]|[0-9]|[\u2E80-\u9FFF]|[\\u4e00-\\u9fa5]|[`~!@#$%^&*()+=|{}':;',\\\\[\\\\]._<>/?~！@#￥%……&*（）\\-\\{\\}\\[\\]——+|{}【】‘；：”“’。，、？])+|@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?|[wap.]{4}|[www.]{4}|[blog.]{5}|[bbs.]{4}|[.com]{4}|[.cn]{3}|[.net]{4}|[.org]{4}|[http://]{7}|[ftp://]{6}$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        return !matcher.matches();
    }

    /**
     * 检查手机号码
     *
     * @param phone 手机号码
     * @return true:正确的手机号码
     */
    public static boolean isPhoneNumber(String phone) {
        return notEmpty(phone) && phone.length() == 11;
    }

    public static boolean checkPhone(String phone) {
        Pattern pattern = Pattern.compile("^(13[0-9]|15[0-9]|18[0-9]|17[0-9])\\d{8}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    /**
     * 昵称的正则:过滤四个内容 <> /\
     * @param nickName
     * @return
     */
    public static boolean checkNickName(String nickName){
        Pattern pattern = Pattern.compile("[^\\\\/<>*\\\\\\\\]+");
        Matcher matcher = pattern.matcher(nickName);
        return matcher.matches();
    }

    /**
     * 设置 textView 控件的值
     *
     * @param text   需要设置的值
     * @param target textView
     */
    public static void setText(String text, TextView target) {
        if (isEmpty(text)) {
            target.setText("");
        } else {
            target.setText(text);
        }
    }

    /**
     * 设置 textView 控件的值 值为空时隐藏VIEW
     *
     * @param text   需要设置的值
     * @param target textView
     */
    public static void setTextEmptyGone(String text, TextView target) {
        if (isEmpty(text)) {
            //Utils.setGone(target, true);
        } else {
            target.setText(text);
            //Utils.setGone(target, false);
        }
    }

    /**
     * 设置 textView 控件的值 值为空时隐藏VIEW
     *
     * @param text   需要设置的值
     * @param target textView
     */
    public static void setTextEmptyInvisible(String text, TextView target) {
        if (isEmpty(text)) {
            //Utils.setInvisible(target, true);
        } else {
            target.setText(text);
            //Utils.setInvisible(target, false);
        }
    }

    /**
     * replace shop name contains "(" ;
     *
     * @param resultStr 返回字符串
     * @return String
     */
    public static String replaceShopName(String resultStr) {
        return resultStr.replace("（", "").replace("(", "")
                .replace("）", "").replace(")", "");
    }

    /**
     * 字符串数组转字符串
     *
     * @param strings 需要转换的字符串数组
     * @return String
     */
    public static StringBuilder formatArrayStrings(String[] strings, int formatSize, String format) {
        StringBuilder buffer = new StringBuilder();
        if (strings != null && strings.length > 0) {
            int size = strings.length > formatSize ? formatSize : strings.length;//只取前两个标签
            for (int i = 0; i < size; i++) {
                buffer.append(strings[i]).append(format);
            }
        }
        return buffer;
    }

    public static String StringArrayToString(String[] strings) {
        StringBuffer sb = new StringBuffer();

        if (strings != null && strings.length > 0) {
            int size = strings.length > 2 ? 2 : strings.length;//只取前两个标签
            for (int i = 0; i < size; i++) {
                sb.append(strings[i]).append(" ");
            }
        }
        return sb.toString();
    }

    //byteArr转hexString
    static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String bytes2HexString(byte[] bytes) {
        if (bytes == null) return null;
        int len = bytes.length;
        if (len <= 0) return null;
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    public static void setTextViewText(TextView tv, String text) {
        setTextViewText(tv, text, "");
    }

    public static void setTextViewText(TextView tv, String text, String defaultStr) {
        if (StringHelper.notEmpty(text)) {
            tv.setText(text);
        } else {
            tv.setText(defaultStr);
        }
    }

    public static String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes" : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F))
                + "KB" : (var0 < 1073741824L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F))
                + "MB" : (var0 < 0L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F))
                + "GB" : "error")));
    }

    static String[] units = {"", "十", "百", "千", "万", "十万", "百万", "千万", "亿", "十亿", "百亿", "千亿", "万亿"};
    static char[] numArray = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};

    /**
     * 将整数转换成汉字数字
     *
     * @param num 需要转换的数字
     * @return 转换后的汉字
     */
    public static String formatInteger(int num) {
        if (num >= 10 && num <= 20) {
            switch (num) {
                case 10:
                    return "十";
                case 11:
                    return "十一";
                case 12:
                    return "十二";
                case 13:
                    return "十三";
                case 14:
                    return "十四";
                case 15:
                    return "十五";
                case 16:
                    return "十六";
                case 17:
                    return "十七";
                case 18:
                    return "十八";
                case 19:
                    return "十九";
                case 20:
                    return "二十";
            }
        }

        if (num == 30 || num == 40 || num == 50 || num == 60 || num == 70 || num == 80 || num == 90) {
            switch (num) {
                case 30:
                    return "三十";
                case 40:
                    return "四十";
                case 50:
                    return "五十";
                case 60:
                    return "六十";
                case 70:
                    return "七十";
                case 80:
                    return "八十";
                case 90:
                    return "九十";
            }
        }

        char[] val = String.valueOf(num).toCharArray();
        int len = val.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String m = val[i] + "";
            int n = Integer.valueOf(m);
            boolean isZero = n == 0;
            String unit = units[(len - 1) - i];
            if (isZero) {
                if ('0' == val[i - 1]) {
                    continue;
                } else {
                    sb.append(numArray[n]);
                }
            } else {
                sb.append(numArray[n]);
                sb.append(unit);
            }
        }
        return sb.toString();
    }

    //保留两位小数 up 四舍五入
    public static String formatDouble(double d, boolean up) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(2);

        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(up ? RoundingMode.UP : RoundingMode.DOWN);

        return nf.format(d);
    }

    //保留两位小数 up 四舍五入  数字格式是科学计数法
    public static String formatDouble(double d, boolean up,int count) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(count);

        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(up ? RoundingMode.UP : RoundingMode.DOWN);

        return nf.format(d).replace(",","");
    }

}
