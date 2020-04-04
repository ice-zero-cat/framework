package github.com.icezerocat.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zmj
 * On 2019/8/19.
 *
 * @author 0.0.0
 */
@Slf4j
public class DateUtil {

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
     * 格式化时间(Date 转换成String)
     *
     * @param date   时间
     * @param format 时间格式 如： DEFAULT_FORMAT= "yyyy-MM-dd HH:mm:ss"
     * @return 字符串
     */
    public static String format(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 字符串格式化为时间
     *
     * @param dateStr 时间字符串
     * @param format  时间格式 如：DEFAULT_FORMAT1 = "yyyy/MM/dd HH:mm:ss";// 时间格式1
     * @return 日期对象
     */
    public static Date parseDate(String dateStr, String format) {
        Date date = null;
        if (!StringUtils.isEmpty(dateStr)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
        }
        return date;
    }
}
