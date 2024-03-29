package top.kaluna.modbusTcp.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Yuery
 * @date 2022/2/23/0023 - 16:37
 */
public class DateUtil {
    public static Date getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR, -12);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }
    public static Date getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR, 11);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }
    public static Date getNowTime(){
        return new Date();
    }
    /**
     *
     * @return 获取某年的第一天
     */
    public static Date getCurrYearFirst() {
        Calendar currCal = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, currCal.get(Calendar.YEAR));
        return calendar.getTime();
    }
    /**
     * @return 获取某年的最后一天
     */
    public static Date getCurrYearLast(){
        Calendar currCal = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, currCal.get(Calendar.YEAR));
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }
    /**
     * @return 获取过去N小时的开始时间
     */
    public static Date LastNHoursStart(int past){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.HOUR_OF_DAY,c.get(Calendar.HOUR_OF_DAY) - past);
        return c.getTime();
    }
    public static Date LastNHoursStartAt00mm00ss00sss(int past){
        Calendar c = Calendar.getInstance();
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime localDateTime1 = localDateTime.withSecond(0);
        LocalDateTime localDateTime2 = localDateTime1.withMinute(0);
        LocalDateTime localDateTime3 = localDateTime2.withNano(0);
        Instant instant = Instant.ofEpochSecond(localDateTime3.toEpochSecond(ZoneOffset.ofHours(8)));
        // 获得 Date
        Date date = Date.from(instant);
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY,c.get(Calendar.HOUR_OF_DAY) - past);
        return c.getTime();
    }
    public static Date LastNHoursStartOnTheDate(Date date, int past){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY,c.get(Calendar.HOUR_OF_DAY) - past);
        return c.getTime();
    }
}
