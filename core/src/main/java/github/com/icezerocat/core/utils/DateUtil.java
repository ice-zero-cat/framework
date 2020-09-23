package github.com.icezerocat.core.utils;


import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by zmj
 * On 2019/8/19.
 *
 * @author 0.0.0
 */
@SuppressWarnings("unused")
public class DateUtil {

    public final static String yyyy_MM_dd = "yyyy-MM-dd";
    public final static String DATE_SLASH = "yyyy/MM/dd";
    public final static String DATE_CHINESE = "yyyy年MM月dd日";

    public final static String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_TIME_HOURS = "yyyy-MM-dd HH";
    public final static String DATE_TIME_MINUTES = "yyyy-MM-dd HH:mm";
    public final static String DATE_TIME_SLASH = "yyyy/MM/dd HH:mm:ss";
    public final static String DATE_TIME_CHINESE = "yyyy年MM月dd日 HH时mm分ss秒";

    public final static String DATE_TIME_MILLION = "yyyy-MM-dd HH:mm:ss:SSS";

    public final static String yyyy = "yyyy";
    public final static String yyyyMM = "yyyyMM";
    public final static String yyyyMMdd = "yyyyMMdd";
    public final static String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public final static String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";

    public final static String ZERO_TIME = " 00:00:00";
    public final static String ZERO_TIME_MILLION = " 00:00:00:000";
    public final static String ZERO_TIME_WITHOUT_HOURS = ":00:00";
    public final static String ZERO_TIME_WITHOUT_MINUTES = ":00";
    private final static Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");


    /**
     * 自动解析字符串获取日期
     *
     * @param dateStr 日期字符串
     * @return 日期
     */
    public static Date getDate(String dateStr) {
        return parse(dateStr, getDateFormat(dateStr));
    }

    /**
     * 日期对象转换为yyyymmdd的字符串形式
     *
     * @param date 日期对象
     * @return 日期字符串
     */
    public static String fortmat2yyyyMMdd(Date date) {
        return formatDate(date, yyyyMMdd);
    }

    /**
     * 日期对象转换为yyyy_mm_dd的字符串形式
     *
     * @param date 日期对象
     * @return 日期字符串
     */
    public static String fortmat2yyyy_MM_dd(Date date) {
        return formatDate(date, yyyy_MM_dd);
    }

    /**
     * 日期对象转换为yyyymmddhhmmss的字符串形式
     *
     * @param date 日期对象
     * @return 日期字符串
     */
    public static String fortmat2yyyyMMddHHmmss(Date date) {
        return formatDate(date, yyyyMMddHHmmss);
    }

    /**
     * 格式化日期
     *
     * @param date    日期对象
     * @param pattern 格式表达式
     * @return 日期字符串
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return df.format(date);
    }

    /**
     * yyyymmddhhmmss形式的字符串转换为日期对象
     *
     * @param date 日期字符串
     * @return 日期字符串
     */
    public static Date parse2yyyyMMddHHmmss(String date) {
        return parseDate(date, yyyyMMddHHmmss);
    }

    /**
     * 转换日期
     *
     * @param date    日期字符串
     * @param pattern 格式表达式
     * @return 日期对象
     */
    public static Date parseDate(String date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        try {
            return df.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 当前时间戳转换为秒的字符串形式
     *
     * @return 日期字符串
     */
    public static String timestamp2string() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 字符串转成日期、时间格式
     *
     * @param dateString 日期字符串
     * @param pattern    格式化类型，默认为yyyy-MM-dd，其它使用DateUtils.xxx
     * @return Date
     */
    public static Date parse(String dateString, String pattern) {
        if (StringUtils.isBlank(dateString)) {
            return null;
        } else {
            dateString = dateString.trim();
            if (StringUtils.isBlank(pattern)) {
                pattern = yyyy_MM_dd;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            try {
                return simpleDateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 字符串转成日期（yyyy-MM-dd）格式
     *
     * @param dateString 日期字符串
     * @return Date
     */
    public static Date parseDate(String dateString) {
        return parse(dateString, null);
    }

    /**
     * 字符串转成时间（yyyy-MM-dd HH:mm:ss）格式
     *
     * @param dateString 日期字符串
     * @return Date
     */
    public static Date parseDateTime(String dateString) {
        if (StringUtils.isBlank(dateString)) {
            return null;
        } else {
            dateString = dateString.trim();
            if (dateString.length() == DATE_TIME_HOURS.length()) {
                return parse(dateString, DATE_TIME_HOURS);
            } else if (dateString.length() == DATE_TIME_MINUTES.length()) {
                return parse(dateString, DATE_TIME_MINUTES);
            } else if (dateString.length() == DATE_TIME_MILLION.length()) {
                return parse(dateString, DATE_TIME_MILLION);
            } else {
                return parse(dateString, DATE_TIME);
            }
        }
    }

    /**
     * 时间转字符串
     *
     * @param date    时间
     * @param pattern 格式化类型，默认为yyyy-MM-dd HH:mm:ss，其它使用DateUtils.xxx
     * @return 字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return "";
        } else {
            if (StringUtils.isBlank(pattern)) {
                pattern = DATE_TIME;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(date);
        }
    }

    /**
     * 时间转日期字符串（yyyy-MM-dd）
     *
     * @param date 时间
     * @return 字符串
     */
    public static String formatDate(Date date) {
        return format(date, yyyy_MM_dd);
    }

    /**
     * 时间转日期字符串（yyyy-MM-dd HH:mm:ss）
     *
     * @param date 时间
     * @return 日期字符串
     */
    public static String formatDateTime(Date date) {
        return format(date, null);
    }

    /**
     * 将日期格式转换成时间（yyyy-MM-dd HH:mm:ss）格式
     *
     * @param dateString 日期字符串
     * @return 日期字符串
     */
    public static String dateToDateTime(String dateString) {
        if (StringUtils.isBlank(dateString)) {
            return "";
        } else {
            dateString = dateString.trim();
            if (dateString.length() == yyyy_MM_dd.length()) {
                return dateString + ZERO_TIME;
            } else if (dateString.length() == DATE_TIME_HOURS.length()) {
                return dateString + ZERO_TIME_WITHOUT_HOURS;
            } else if (dateString.length() == DATE_TIME_MINUTES.length()) {
                return dateString + ZERO_TIME_WITHOUT_MINUTES;
            } else if (dateString.length() == DATE_TIME_MILLION.length()) {
                return dateString.substring(0, DATE_TIME.length());
            } else {
                return dateString;
            }
        }
    }

    /**
     * 将时间（yyyy-MM-dd HH:mm:ss）转换成日期（yyyy-MM-dd）
     *
     * @param dateTime 时间
     * @return Date
     */
    public static Date dateTimeToDate(Date dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return parseDate(formatDate(dateTime));
        }
    }

    /**
     * 日期加减天数
     *
     * @param date 日期，为空时默认当前时间，包括时分秒
     * @param days 加减的天数
     * @return 日期
     */
    public static Date dateAdd(Date date, int days) {
        if (date == null) {
            date = new Date();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * 日期加减多少月
     *
     * @param date   日期，为空时默认当前时间，包括时分秒
     * @param months 加减的月数
     * @return 日期
     */
    public static Date monthAdd(Date date, int months) {
        if (date == null) {
            date = new Date();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }


    /**
     * 时间比较
     * <p>如果date大于compareDate返回1，小于返回-1，相等返回0</p>
     *
     * @param date        日期
     * @param compareDate 比较日期
     * @return 比较结果0/1
     */
    public static int dateCompare(Date date, Date compareDate) {
        Calendar cal = Calendar.getInstance();
        Calendar compareCal = Calendar.getInstance();
        cal.setTime(date);
        compareCal.setTime(date);
        return cal.compareTo(compareCal);
    }


    /**
     * 获取两个日期相差的天数，不包含今天
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 天数
     */
    public static int dateBetween(Date startDate, Date endDate) {
        Date dateStart = parse(format(startDate, yyyy_MM_dd), yyyy_MM_dd);
        Date dateEnd = parse(format(endDate, yyyy_MM_dd), yyyy_MM_dd);
        return (int) (((dateEnd != null ? dateEnd.getTime() : 0) - (dateStart != null ? dateStart.getTime() : 0)) / 1000 / 60 / 60 / 24);
    }


    /**
     * 获取两个日期相差的天数，包含今天
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 天数
     */
    public static int dateBetweenIncludeToday(Date startDate, Date endDate) {
        return dateBetween(startDate, endDate) + 1;
    }

    /**
     * 获取指定年月的第一天
     *
     * @param year  年
     * @param month 月
     * @return 年月的第一天
     */
    public static Date getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //设置时
        cal.set(Calendar.HOUR_OF_DAY, 0);
        //设置分
        cal.set(Calendar.MINUTE, 0);
        //设置秒
        cal.set(Calendar.SECOND, 0);
        //获取某月最小天数
        int firstDay = cal.getMinimum(Calendar.DATE);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        return cal.getTime();
    }

    /**
     * 获取指定年月的第一天
     *
     * @param year        年
     * @param month       月
     * @param differMonth 相差月份
     * @return 年月的第一天
     */
    public static Date getFirstDayOfMonth(int year, int month, int differMonth) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month + differMonth - 1);
        //设置时
        cal.set(Calendar.HOUR_OF_DAY, 0);
        //设置分
        cal.set(Calendar.MINUTE, 0);
        //设置秒
        cal.set(Calendar.SECOND, 0);
        //获取某月最小天数
        int firstDay = cal.getMinimum(Calendar.DATE);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        return cal.getTime();
    }

    /**
     * 获取指定年月，次月的一天
     *
     * @param year  年
     * @param month 月
     * @return 年月的最后一天
     */
    public static Date getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //设置时
        cal.set(Calendar.HOUR_OF_DAY, 23);
        //设置分
        cal.set(Calendar.MINUTE, 59);
        //设置秒
        cal.set(Calendar.SECOND, 59);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        return cal.getTime();
    }

    /**
     * 获取指定年月，次月的一天
     *
     * @param year        年
     * @param month       月
     * @param differMonth 相差月份
     * @return 年月的最后一天
     */
    public static Date getLastDayOfMonth(int year, int month, int differMonth) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month + differMonth - 1);
        //设置时
        cal.set(Calendar.HOUR_OF_DAY, 23);
        //设置分
        cal.set(Calendar.MINUTE, 59);
        //设置秒
        cal.set(Calendar.SECOND, 59);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        return cal.getTime();
    }

    /**
     * 常规自动日期格式识别
     *
     * @param str 时间字符串
     * @return Date  日期格式字符串
     */
    private static String getDateFormat(String str) {
        boolean year = false;
        if (pattern.matcher(str.substring(0, 4)).matches()) {
            year = true;
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        if (!year) {
            if (str.contains("月") || str.contains("-") || str.contains("/")) {
                if (Character.isDigit(str.charAt(0))) {
                    index = 1;
                }
            } else {
                index = 3;
            }
        }
        for (int i = 0; i < str.length(); i++) {
            char chr = str.charAt(i);
            if (Character.isDigit(chr)) {
                if (index == 0) {
                    sb.append("y");
                }
                if (index == 1) {
                    sb.append("M");
                }
                if (index == 2) {
                    sb.append("d");
                }
                if (index == 3) {
                    sb.append("H");
                }
                if (index == 4) {
                    sb.append("m");
                }
                if (index == 5) {
                    sb.append("s");
                }
                if (index == 6) {
                    sb.append("S");
                }
            } else {
                if (i > 0) {
                    char lastChar = str.charAt(i - 1);
                    if (Character.isDigit(lastChar)) {
                        index++;
                    }
                }
                sb.append(chr);
            }
        }
        return sb.toString();
    }
}
