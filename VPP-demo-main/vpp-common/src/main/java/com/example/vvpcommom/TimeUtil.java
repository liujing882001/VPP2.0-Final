package com.example.vvpcommom;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * 超详细的时间工具类
 *
 * @author Administrator
 */
public class TimeUtil {

    private static final DateTimeFormatter formatterYmd_threadSafety = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter formatterYmdHHmm_threadSafety = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00");
    private final static SimpleDateFormat formatterYmdHH = new SimpleDateFormat("yyyy-MM-dd HH");
    private final static SimpleDateFormat formatterYmdHHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
    private final static SimpleDateFormat formatterHH = new SimpleDateFormat("HH");
    private final static SimpleDateFormat formatterYm = new SimpleDateFormat("yyyy-MM");
    private final static SimpleDateFormat formatterY = new SimpleDateFormat("yyyy");
    private final static SimpleDateFormat formatterMonth = new SimpleDateFormat("MM");
    private final static SimpleDateFormat formatterDay = new SimpleDateFormat("dd");
    private final static SimpleDateFormat formatterYmNumber = new SimpleDateFormat("yyyyMM");

    /**
     * @param date
     * @return
     * @description: 获得当天最小时间
     * @author: Jeff
     * @date: 2019年12月21日
     */
    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()),
                ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @param date
     * @return
     * @description: 获得当天最大时间
     * @author: Jeff
     * @date: 2019年12月21日
     */
    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()),
                ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写
     *
     * @param sformat
     * @return
     */
    public static String getUserDate(String sformat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(sformat);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * date类型进行格式化输出（返回类型：String）
     *
     * @param date
     * @return
     */
    public static String dateFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 将"2015-08-31 21:08:06"型字符串转化为Date
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date date = null;
        try {
            date = formatter.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 时间前推或后推分钟,其中JJ表示分钟.
     *
     * @param StringTime：时间
     * @param minute：分钟（有正负之分）
     * @return
     */
//    public static String getPreTime(String StringTime, String minute) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        String mydate1 = "";
//        try {
//            Date date1 = format.parse(StringTime);
//            long Time = (date1.getTime() / 1000) + Integer.parseInt(minute) * 60;
//            date1.setTime(Time * 1000);
//            mydate1 = format.format(date1);
//        } catch (Exception e) {
//            return "";
//        }
//        return mydate1;
//    }

    public static String getPreTime(String StringTime, String minute) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(StringTime, dateTimeFormatter);
        long minutes = Long.parseLong(minute);

        LocalDateTime localDateTime = parse.plusMinutes(minutes);
        return localDateTime.format(dateTimeFormatter);

    }

    public static Date getPreMonth(Date date, int month) {
        Calendar minCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        minCal.setTime(date);
        minCal.set(Calendar.AM_PM, Calendar.AM);
        minCal.set(Calendar.HOUR, 0);
        minCal.set(Calendar.MINUTE, 0);
        minCal.set(Calendar.SECOND, 0);
        minCal.set(Calendar.MILLISECOND, 0);
        minCal.add(Calendar.MONTH, month + 1);
        minCal.set(Calendar.DAY_OF_MONTH, minCal.getActualMinimum(Calendar.DAY_OF_MONTH));
        minCal.add(Calendar.MILLISECOND, -1);

        return minCal.getTime();
    }

    public static Date getPreDay(Date date, int day) {
        return stringToDate(getPreTime(dateFormat(date), 60 * 24 * day + ""));
    }

    /**
     * 获取指定日期的月初和月末日子
     *
     * @param day 日期20200202
     */
    public static Date getMonthStart(Date day) {
        try {
            LocalDateTime now = day.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime first = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0, 0, 0);
            LocalDateTime last = LocalDateTime.of(first.with(TemporalAdjusters.lastDayOfMonth()).getYear(),
                    first.with(TemporalAdjusters.lastDayOfMonth()).getMonth(),
                    first.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(), 23, 59, 59);
            return Date.from(first.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
        }
        return null;
    }

    public static Date getMonthEnd(Date day) {
        try {


            LocalDateTime now = day.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime first = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0, 0, 0);
            LocalDateTime last = LocalDateTime.of(first.with(TemporalAdjusters.lastDayOfMonth()).getYear(),
                    first.with(TemporalAdjusters.lastDayOfMonth()).getMonth(),
                    first.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(), 23, 59, 59);
            return Date.from(last.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
        }
        return null;
    }

    public static Date getYearStart(Date day) {
        try {

            LocalDateTime now = day.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime last = LocalDateTime.of(now.getYear(),
                    Month.JANUARY,
                    1, 0, 0, 0);
            return Date.from(last.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
        }
        return null;
    }

    public static Date getYearEnd(Date day) {
        try {

            LocalDateTime now = day.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime last = LocalDateTime.of(now.getYear(),
                    Month.DECEMBER,
                    31, 23, 59, 59);
            return Date.from(last.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
        }
        return null;
    }

    public static Date dateAddDay(Date date, int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        date = calendar.getTime();
        return date;
    }

    public static Date dateAddMinutes(Date date, int minute) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        date = calendar.getTime();
        return date;
    }

    public static Date dateAddHours(Date date, int hour) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hour);
        date = calendar.getTime();
        return date;
    }

    public static Date dateAddMonths(Date date, int month) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        date = calendar.getTime();
        return date;
    }

    public static Date dateAddYears(Date date, int year) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
        date = calendar.getTime();
        return date;
    }

    public static Date dateAddSeconds(Date date, int second) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, second);
        date = calendar.getTime();
        return date;
    }

    public static Date strToDateFormat(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            formatter.setLenient(false);
            Date newDate = formatter.parse(date);

            SimpleDateFormat formatter_ymd = new SimpleDateFormat("yyyy-MM-dd");
            formatter_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            return formatter_ymd.parse(formatter_ymd.format(newDate));
        } catch (Exception e) {

            return null;
        }

    }

    public static Date strToDateFormat_ymd_hms(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            formatter.setLenient(false);
            Date newDate = formatter.parse(date);
            SimpleDateFormat formatter_ymd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            return formatter_ymd.parse(formatter_ymd.format(newDate));
        } catch (Exception e) {
            return null;
        }

    }

    public static Date strFormat(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, formatterYmd_threadSafety);
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            return null;
        }
    }

    public static String toYmdStr(Date date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime parse = LocalDateTime.ofInstant(date.toInstant(),ZoneId.of("+8"));

        return parse.format(dateTimeFormatter);
    }

    public static String toHHmmStr(Date date){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime parse = LocalDateTime.ofInstant(date.toInstant(),ZoneId.systemDefault());
        return parse.format(dateTimeFormatter);
    }


    public static String toHHStr(Date date) {
        try {
            return formatterHH.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    public static String toYmStr(Date date) {
        try {
            return formatterYm.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    public static String toYStr(Date date) {
        try {
            return formatterY.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    public static String toMonthStr(Date date) {
        try {
            return formatterMonth.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    public static String toDayStr(Date date) {
        try {
            return formatterDay.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    public static String toYmNumberStr(Date date) {
        try {
            return formatterYmNumber.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    public static String toYmdHHStr(Date date) {
        try {
            return formatterYmdHH.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    public static String toYmdHHmmStr(Date date) {
        try {
            return formatterYmdHHmm.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    public static String toYmdHHmmStr_threadSafety(Date date) {
        try {
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return localDateTime.format(formatterYmdHHmm_threadSafety);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date strYmFormat(String date) {
        try {
            return formatterYm.parse(date);
        } catch (Exception e) {

            return null;
        }

    }

    public static Date strYFormat(String date) {
        try {
            return formatterY.parse(date);
        } catch (Exception e) {

            return null;
        }

    }


    public static List<Date> truncateToSplit15Minutes(Date start, Date end) {

        List<Date> result = new ArrayList<>();

        LocalDateTime startLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getTime()), ZoneOffset.of("+8"));

        LocalDateTime endLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(end.getTime()), ZoneOffset.of("+8"));


        LocalDateTime lastQuarter = startLt.truncatedTo(ChronoUnit.HOURS)
                .plusMinutes(15 * (startLt.getMinute() / 15));

        while (lastQuarter.isBefore(endLt)) {
            result.add(new Date(lastQuarter.toInstant(ZoneOffset.ofHoursMinutes(+8, 0)).toEpochMilli()));
            lastQuarter = lastQuarter.plusMinutes(15);
        }


        return result;
    }

    public static List<Date> truncateToSplitDay(Date start, Date end) {

        List<Date> result = new ArrayList<>();

        LocalDateTime startLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getTime()), ZoneOffset.of("+8"));

        LocalDateTime endLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(end.getTime()), ZoneOffset.of("+8"));

        LocalDateTime lastQuarter = startLt.truncatedTo(ChronoUnit.DAYS);

        while (lastQuarter.isBefore(endLt)) {
            result.add(new Date(lastQuarter.toInstant(ZoneOffset.ofHoursMinutes(+8, 0)).toEpochMilli()));
            lastQuarter = lastQuarter.plusDays(1);
        }
        return result;
    }

    public static List<Date> truncateToSplitDayContainEndDate(Date start, Date end) {

        List<Date> result = new ArrayList<>();

        LocalDateTime startLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getTime()), ZoneOffset.of("+8"));

        LocalDateTime endLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(end.getTime()), ZoneOffset.of("+8"));

        LocalDateTime lastQuarter = startLt.truncatedTo(ChronoUnit.DAYS);

        while (!lastQuarter.isAfter(endLt)) {
            result.add(new Date(lastQuarter.toInstant(ZoneOffset.ofHoursMinutes(+8, 0)).toEpochMilli()));
            lastQuarter = lastQuarter.plusDays(1);
        }
        return result;
    }

    /**
     * 按照月份分割一段时间
     *
     * @param startTime 开始时间戳(毫秒)
     * @param endTime   结束时间戳(毫秒)
     */
    public static List<Long> getIntervalTimeByMonth(Date startTime, Date endTime) {

        List<Long> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        dateList.add(calendar.getTimeInMillis());
        while (calendar.getTimeInMillis() < endTime.getTime()) {
            // 当月底
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            if (calendar.getTimeInMillis() >= endTime.getTime()) {
                dateList.add(endTime.getTime());
                break;
            }
            // 下月初
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            dateList.add(calendar.getTimeInMillis());
        }
        return dateList;
    }


    public static List<Date> truncateToSplitYear(Date start, Date end) {

        List<Date> result = new ArrayList<>();

        LocalDateTime startLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getTime()), ZoneOffset.of("+8"));

        LocalDate endLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(end.getTime()), ZoneOffset.of("+8")).toLocalDate();

        LocalDate lastQuarter = startLt.toLocalDate().withDayOfYear(1);

        while (lastQuarter.isBefore(endLt) || lastQuarter.isEqual(endLt)) {
            result.add(Date.from(lastQuarter.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            lastQuarter = lastQuarter.plusYears(1);
        }
        return result;
    }

    public static List<Date> truncateToSplitMonth(Date start, Date end) {

        List<Date> result = new ArrayList<>();

        LocalDateTime startLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getTime()), ZoneOffset.of("+8"));

        LocalDate endLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(end.getTime()), ZoneOffset.of("+8")).toLocalDate();

        LocalDate lastQuarter = startLt.toLocalDate().withDayOfMonth(1);

        while (lastQuarter.isBefore(endLt) || lastQuarter.isEqual(endLt)) {
            result.add(Date.from(lastQuarter.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            lastQuarter = lastQuarter.plusMonths(1);
        }
        return result;
    }


    /**
     * 按照指定小时分割时间段
     *
     * @param dateType 类型 M/D/H/N -->每月/每天/每小时/每分钟
     * @param time     指定分割(如：1、2、3、4)
     * @return
     */
    public static List<String> findDates(String dateType, Date dBegin, Date dEnd, int time) throws Exception {
        List<String> listDate = new ArrayList<>();
        listDate.add(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dEnd);
        while (calEnd.after(calBegin)) {
            if ("H".equals(dateType)) {
                calBegin.add(Calendar.HOUR, time);
            }
            if ("M".equals(dateType)) {
                calBegin.add(Calendar.MONTH, time);
            }
            if ("D".equals(dateType)) {
                calBegin.add(Calendar.DATE, time);
            }
            if ("N".equals(dateType)) {
                calBegin.add(Calendar.MINUTE, time);
            }
            if (calEnd.after(calBegin)) {
                listDate.add(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calBegin.getTime()));
            } else {
                // listDate.add(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calEnd.getTime()));
            }

        }
        return listDate;
    }


    public static Date truncateTo(long time) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneOffset.of("+8"));
        LocalDateTime lastQuarter = localDateTime.truncatedTo(ChronoUnit.HOURS);
        return new Date(lastQuarter.toInstant(ZoneOffset.ofHoursMinutes(+8, 0)).toEpochMilli());

    }

    /**
     * 根据时间 和时间格式 校验是否正确
     *
     * @param length 校验的长度
     * @param sDate  校验的日期
     * @param format 校验的格式
     * @return
     */
    public static boolean isLegalDate(int length, String sDate, String format) {
        int legalLen = length;
        if ((sDate == null) || (sDate.length() != legalLen)) {
            return false;
        }
        DateFormat formatter = new SimpleDateFormat(format);
        try {
            Date date = formatter.parse(sDate);
            return sDate.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将日期格式字符串转换为指定时间格式
     *
     * @param strDate
     * @return
     */
    public static Date strDDToDate(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        // 国内时区是GMT+8
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }


    /**
     * 两个时间之间相差多少小时
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static double getDifferHour(Date startDate, Date endDate) {
        long hourM = 1000 * 60 * 60;
        Long differ = endDate.getTime() - startDate.getTime();
        double hour = differ.doubleValue() / hourM;
        BigDecimal b = new BigDecimal(hour);
        Double hour1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//第一个参数是保留小数的位数
        return hour1;
    }

    /**
     * 判断时间是否在时间段内
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }

    /**
     * 比较一个 HH:mm:ss 是否在一个时间段内
     * 如：14:33:00 是否在 09:30:00 和 12:00:00 内
     */
    public static boolean timeIsInRound(String str1, String start, String end) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date now = null;
        Date beginTime = null;
        Date endTime = null;

        try {
            now = df.parse(str1);
            beginTime = df.parse(start);
            endTime = df.parse(end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return belongCalendar(now, beginTime, endTime);
    }

    /**
     * 14:33:00 是否在 09:30:00 - 12:00:00 内
     *
     * @param str1  14:33:00
     * @param round 09:30:00 - 12:00:00
     */
    public static boolean timeIsInRound(String str1, String round) {
        String[] roundTime = round.split("-");
        return timeIsInRound(str1, roundTime[0], roundTime[1]);
    }


    /**
     * date2比date1多的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {//同一年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else {// 不同年
//            System.out.println("判断day2 - day1 : " + (day2 - day1));
            return day2 - day1;
        }
    }

    public static Minutes15Model convert15MinutesStr(Date date) {
        LocalDateTime startLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneOffset.of("+8"));
        LocalDateTime lastQuarter = startLt.truncatedTo(ChronoUnit.HOURS)
                .plusMinutes(15 * (startLt.getMinute() / 15));
        LocalTime start = lastQuarter.toLocalTime();
        String startStr = start.toString();
        LocalTime end = start.plusMinutes(15);

        Minutes15Model vm = new Minutes15Model();
        vm.setEndTime(end.toString());
        vm.setStartTime(start.toString());
        vm.setTimeScope(startStr + "-" + end.toString());
        return vm;
    }

    /**
     * 计算两个日期的月份差
     *
     * @param startTime
     * @param endTime
     * @return
     * @throws ParseException
     */
    public static long getMonthDiff(String startTime, String endTime) throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM");
        long monthday;
        Calendar starCal = Calendar.getInstance();
        starCal.setTime(f.parse(startTime));

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(f.parse(endTime));

        monthday = ((endCal.get(Calendar.YEAR) - starCal.get(Calendar.YEAR)) * 12 + (endCal.get(Calendar.MONTH) - starCal.get(Calendar.MONTH)));

        //如果计算出等于0，说明要加一个月
        if (starCal.get(Calendar.DATE) <= endCal.get(Calendar.DATE)) {
            monthday = monthday + 1;
        }
        return monthday;
    }

    public static Date getLastDay(int i) {
        Calendar cld = Calendar.getInstance();
        cld.add(Calendar.DATE, -i);
        return cld.getTime();
    }

    /**
     * 截取年
     */
    public static String getThisYear() {
        return formatterY.format(new Date());
    }

    public static Date getLastYearThisDay() {
        Calendar cal_1 = Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.YEAR, -1);
        return cal_1.getTime();
    }

    /**
     * 截取月
     */
    public static String getThisMonth() {
        return formatterYm.format(new Date());
    }

    public static Date getLastMontOnDay() {
        Calendar cal_1 = Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH, 1);//设置为1号
        cal_1.set(Calendar.HOUR_OF_DAY, 0);
        cal_1.set(Calendar.MINUTE, 0);
        cal_1.set(Calendar.SECOND, 0);
        return cal_1.getTime();
    }

    public static Date getLastMontThisDay() {
        Calendar cal_1 = Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, -1);
        return cal_1.getTime();
    }

    /**
     * 获取一个月最后一天
     *
     * @param yearMonth 2022-12
     * @return
     */
    public static String getMonthLastDay(String yearMonth) {

        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, Integer.parseInt(yearMonth.split("-")[0]));
        //设置月份
        cal.set(Calendar.MONTH, Integer.parseInt(yearMonth.split("-")[1]));
        //获取当月最小值
        int lastDay = cal.getMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中的月份，当月+1月-1天=当月最后一天
        cal.set(Calendar.DAY_OF_MONTH, lastDay - 1);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfMonth = sdf.format(cal.getTime());
        return lastDayOfMonth;
    }

    /**
     * String日期转Date日期
     *
     * @param date
     * @return
     */
    public static Date strToDateFormatYMDHMS(String date) {
        try {
            SimpleDateFormat formatter_ymd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            return formatter_ymd.parse(date);
        } catch (Exception e) {
            return null;
        }

    }

    /*** 获取某年第一天日期开始时刻
     * @paramyear 年份
     * @returnDate
     */
    public static Date getYearFirstDay(int year) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        Date yearFirstDay = cal.getTime();
        return yearFirstDay;
    }


    /**
     * 获取某年最后一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getLastOfYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    public static List<Date> demandSplit15Minutes(Date start, Date end) {
        List<Date> result = new ArrayList<>();

        LocalDateTime startLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getTime()), ZoneOffset.of("+8"));
        LocalDateTime endLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(end.getTime()), ZoneOffset.of("+8"));

        LocalDateTime lastQuarter = startLt.truncatedTo(ChronoUnit.HOURS)
                .plusMinutes(15 * (startLt.getMinute() / 15));

        while (!endLt.isBefore(lastQuarter)) {
            Date date = new Date(lastQuarter.toInstant(ZoneOffset.ofHoursMinutes(+8, 0)).toEpochMilli());
            //如果日期不在开始日期之前，则加入结果集
            if (!date.before(start)) {
                result.add(date);
            }
            lastQuarter = lastQuarter.plusMinutes(15);
        }
        return result;
    }

    /**
     * 两个时间之间相差多少分钟
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static double getDifferMinute(Date startDate, Date endDate) {
        long minute = 1000 * 60;
        Long differ = endDate.getTime() - startDate.getTime();
        double m = differ.doubleValue() / minute;
        BigDecimal b = new BigDecimal(m);
        Double m1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//第一个参数是保留小数的位数
        return m1;
    }

    /**
     * 计算两个日期相差的具体月份
     *
     * @param minDate
     * @param maxDate
     * @return
     * @throws ParseException
     */
    public static List<String> getMonthBetween(String minDate, String maxDate) throws ParseException {
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }

        return result;
    }

    /**
     * 开始截止日期合并转为任务编码存放
     *
     * @return
     */
    public static Long startEndDateToStr(Date startDate, Date startTime, Date endTime) {
        try {
            SimpleDateFormat formatter_ymd = new SimpleDateFormat("yyyyMMdd");
            formatter_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat format_hm = new SimpleDateFormat("HHmm");
            format_hm.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            String startDateStr = formatter_ymd.format(startDate);
            String startStr = format_hm.format(startTime);
            String endStr = format_hm.format(endTime);
            return Long.valueOf(startDateStr + startStr + endStr);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 通过输入日期，得到当天每15分钟的时间
     *
     * @param inputDate
     * @return
     */
    public static List<String> getTimeIntervals(String inputDate) {
        List<String> intervals = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(inputDate + " 00:00:00", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(inputDate + " 23:59:59", formatter);

        while (startDateTime.isBefore(endDateTime)) {
            intervals.add(startDateTime.format(formatter));
            startDateTime = startDateTime.plusMinutes(15);
        }

        return intervals;
    }

    /**
     * 判断某个日期列表，是否包含某个时间
     *
     * @param dateList
     * @param targetTime
     * @return
     */
    public static boolean containsTime(List<Date> dateList, String targetTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        for (Date date : dateList) {
            String currentTime = sdf.format(date);

            if (currentTime.equals(targetTime)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取每15分钟的时间列表，并以格式化的字符串（HH:mm:ss）形式返回
     *
     * @return
     */
    public static List<String> generateTimeList() {
        List<String> timeList = new ArrayList<>();

        LocalTime startTime = LocalTime.of(0, 0); // 起始时间

        for (int i = 0; i < 24 * 4; i++) { // 一天有24小时，每小时有4个15分钟
            String formattedTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            timeList.add(formattedTime);

            startTime = startTime.plusMinutes(15);
        }

        return timeList;
    }


    public static Date threeDaysAgoMidnight() {
        try {

            SimpleDateFormat fmt_ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            fmt_ymdhms.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime threeDaysAgoMidnight = now.minus(3, ChronoUnit.DAYS).with(LocalTime.MIN);

            System.out.println("三天前的凌晨时间: " + threeDaysAgoMidnight);
            return fmt_ymdhms.parse(fmt_ymdhms.format(threeDaysAgoMidnight));
        } catch (Exception ex) {
            return null;
        }
    }
}
