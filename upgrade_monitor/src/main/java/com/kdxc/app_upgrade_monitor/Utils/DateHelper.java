package com.kdxc.app_upgrade_monitor.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Author:    Diamond_Lin
 * Version    V1.0
 * Date:      2017/2/16 上午10:30
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2017/2/16      Diamond_Lin            1.0                    1.0
 * Why & What is modified:
 */

public class DateHelper {

    public static String DATA_PATTERN = "yyyy-MM-dd";

    /**
     * @param millis   要转化的毫秒数。
     * @param isWhole  是否强制全部显示小时/分/秒/毫秒。
     * @param isFormat 时间数字是否要格式化，如果true：少位数前面补全；如果false：少位数前面不补全。
     * @return 返回时间字符串：小时/分/秒/毫秒的格式（如：24903600 --> 06小时55分03秒600毫秒）。
     */
    public static String millisToString(long millis, boolean isWhole, boolean isFormat) {
        String h = "";
        String m = "";
        String s = "";
        String mi = "";
        if (isWhole) {
            h = isFormat ? "00小时" : "0小时";
            m = isFormat ? "00分" : "0分";
            s = isFormat ? "00秒" : "0秒";
            mi = isFormat ? "00毫秒" : "0毫秒";
        }

        long temp = millis;

        long hper = 60 * 60 * 1000;
        long mper = 60 * 1000;
        long sper = 1000;

        if (temp / hper > 0) {
            if (isFormat) {
                h = temp / hper < 10 ? "0" + temp / hper : temp / hper + "";
            } else {
                h = temp / hper + "";
            }
            h += "小时";
        }
        temp = temp % hper;

        if (temp / mper > 0) {
            if (isFormat) {
                m = temp / mper < 10 ? "0" + temp / mper : temp / mper + "";
            } else {
                m = temp / mper + "";
            }
            m += "分";
        }
        temp = temp % mper;

        if (temp / sper > 0) {
            if (isFormat) {
                s = temp / sper < 10 ? "0" + temp / sper : temp / sper + "";
            } else {
                s = temp / sper + "";
            }
            s += "秒";
        }
        temp = temp % sper;
        mi = temp + "";

        if (isFormat) {
            if (temp < 100 && temp >= 10) {
                mi = "0" + temp;
            }
            if (temp < 10) {
                mi = "00" + temp;
            }
        }

        mi += "毫秒";
        return h + m + s + mi;
    }


    /**
     * 把日期毫秒转化为字符串
     *
     * @param millis  要转化的日期毫秒数。
     * @param pattern 要转化为的字符串格式（如：yyyy-MM-dd HH:mm:ss）。
     * @return 返回日期字符串。
     */
    public static String millisToStringDate(long millis, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(new Date(millis));
    }

    /**
     * 把日期毫秒转化为字符串（文件名）
     *
     * @param millis  要转化的日期毫秒数。
     * @param pattern 要转化为的字符串格式（如：yyyy-MM-dd HH:mm:ss）。
     * @return 返回日期字符串（yyyy_MM_dd_HH_mm_ss）。
     */
    public static String millisToStringFilename(long millis, String pattern) {
        String dateStr = millisToStringDate(millis, pattern);
        return dateStr.replaceAll("[- :]", "_");
    }

    /**
     * 字符串解析成毫秒数
     *
     * @param str 要转化的日期
     * @return 返回日期的毫秒数
     */
    public static long string2Millis(String str) {
        return string2Millis(str, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 字符串解析成毫秒数
     *
     * @param str 要转化的日期
     * @return 返回日期的毫秒数
     */
    public static long hourMin2Millis(String str) {
        return string2Millis(str, "HH:mm");
    }

    /**
     * 字符串解析成毫秒数
     *
     * @param str     要转化的日期
     * @param pattern 要转化为的字符串格式（如：yyyy-MM-dd HH:mm:ss）
     * @return 返回日期的毫秒数
     */
    public static long string2Millis(String str, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern,
                Locale.getDefault());
        long millis = 0;
        try {
            millis = format.parse(str).getTime();
        } catch (ParseException e) {
            Log.e("FZDateHelper", e.getMessage());
        }
        return millis;
    }

    /**
     * 把一个毫秒数转化成时间字符串
     *
     * @param millis   要转化的毫秒数。
     * @param isWhole  是否强制全部显示小时/分。
     * @param isFormat 时间数字是否要格式化，如果true：少位数前面补全；如果false：少位数前面不补全。
     * @return 返回时间字符串：小时/分/秒/毫秒的格式（如：24903600 --> 06小时55分钟）。
     */
    public static String millisToStringShort(long millis, boolean isWhole,
                                             boolean isFormat) {
        String h = "";
        String m = "";
        if (isWhole) {
            h = isFormat ? "00小时" : "0小时";
            m = isFormat ? "00分钟" : "0分钟";
        }

        long temp = millis;

        long hper = 60 * 60 * 1000;
        long mper = 60 * 1000;
        long sper = 1000;

        if (temp / hper > 0) {
            if (isFormat) {
                h = temp / hper < 10 ? "0" + temp / hper : temp / hper + "";
            } else {
                h = temp / hper + "";
            }
            h += "小时";
        }
        temp = temp % hper;

        if (temp / mper > 0) {
            if (isFormat) {
                m = temp / mper < 10 ? "0" + temp / mper : temp / mper + "";
            } else {
                m = temp / mper + "";
            }
            m += "分钟";
        }

        return h + m;
    }

    public static long oneHourMillis = 60 * 60 * 1000; // 一小时的毫秒数
    public static long oneDayMillis = 24 * oneHourMillis; // 一天的毫秒数
    public static long oneMonthMillis = 30 * oneDayMillis; //一月的毫秒数

    public static String millisToLifeString(String date) {
        return millisToLifeString(string2Millis(date));
    }

    /**
     * 转换当前时间为易用时间格式
     *
     * @param millis 时间毫秒数
     * @return 1小时内用，多少分钟前； 超过1小时，显示多少小时前； 超过24小时，则显示多少天前, 超过30天,显示完整时间。
     */
    public static String millisToLifeString(long millis) {
        long now = SignatureHelper.getTime();
        long todayStart = string2Millis(millisToStringDate(now, "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss");

        // 一小时内
        if (now - millis <= oneHourMillis) {
            String m = millisToStringShort(now - millis, false, false);
            return "".equals(m) ? "1分钟内" : m + "前更新";
        }

        // 大于今天开始开始值，小于今天开始值加一天（即今天结束值）
        else if (millis <= todayStart && millis > todayStart - oneDayMillis) {

            // return "今天 " + millisToStringDate(millis, "HH:mm");
            return (now - millis) / oneHourMillis + "小时前更新";
        }

        // 大于（今天开始值减一天，小于一月）
        else if (millis <= todayStart - oneDayMillis && millis > todayStart - oneMonthMillis) {
            // return "昨天 " + millisToStringDate(millis, "HH:mm");
            return (now - millis) / oneDayMillis + "天前更新";
        } else {

        /*long thisYearStart = string2Millis(millisToStringDate(now, "yyyy"), "yyyy");
        // 大于今天小于今年
        if (millis > thisYearStart) {
            return "上次测试时间:今年"+millisToStringDate(millis, "MM月dd日 HH:mm");
        }*/

            return "更新时间:" + millisToStringDate(millis, "yyyy-MM-dd");
        }
    }

    /**
     * 转换当前时间为易用时间格式
     *
     * @param millis 时间毫秒数
     * @return 一分钟以内显示刚刚，当天显示 上午/下午 几点几分  今天之前加日期
     */
    public static String millisToIMString(long millis) {
        long now = SignatureHelper.getTime();
        long todayStart = string2Millis(millisToStringDate(now, "yyyy-MM-dd"), "yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String str_hour = hour > 12 ? "下午" + (hour - 12) : "上午" + hour;
        String str_min = minute > 9 ? String.valueOf(minute) : "0" + minute;

        // 今天内
        if (millis > todayStart) {
            return str_hour + ":" + str_min;
        } else {
            return month + "月" + day + "日  " + str_hour + ":" + str_min;
        }
    }

    /**
     * 转换当前时间为易用时间格式
     *
     * @param millis 时间毫秒数
     * @return 一分钟以内显示刚刚，当天显示 几点几分  今天之前加日期
     */
    public static String millisToIMStringIs24(long millis) {
        long now = SignatureHelper.getTime();
        long todayStart = string2Millis(millisToStringDate(now, "yyyy-MM-dd"), "yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String str_min = minute > 9 ? String.valueOf(minute) : "0" + minute;

        // 今天内
        if (millis > todayStart) {
            return hour + ":" + str_min;
        } else {
            return month + "月" + day + "日  " + hour + ":" + str_min;
        }
    }

    //时间轴
    public static boolean isSameDay(long time) {
        long now = System.currentTimeMillis();
        long todayStart = string2Millis(millisToStringDate(now, "yyyy-MM-dd"), "yyyy-MM-dd");
        return time >= todayStart;
    }

    //获取当前时间的HH:mm
    public static String getCurrentHour() {
        return millisToStringDate(System.currentTimeMillis(), "HH:mm");
    }

    //获取今天过去的秒数
    public static int getTimeLine() {
        long now = System.currentTimeMillis();
        long todayStart = string2Millis(millisToStringDate(now, "yyyy-MM-dd"), "yyyy-MM-dd");
        return (int) (now - todayStart);
    }

    //获取今天过去的秒数
    public static int getTimeLine(long now) {
        long todayStart = string2Millis(millisToStringDate(now, "yyyy-MM-dd"), "yyyy-MM-dd");
        return (int) (now - todayStart);
    }

    /**
     * 通过时间转星期
     *
     * @param date
     * @return
     */
    public static String getWeekOfDate(Date date) {
        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekOfDays[w];
    }

}
