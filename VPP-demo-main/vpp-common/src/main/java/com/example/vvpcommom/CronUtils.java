package com.example.vvpcommom;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cronutils.model.CronType.QUARTZ;

/**
 * ClassName : CronUtils
 * Date : 2021/6/3 3:10 下午
 */
@Component
public class CronUtils {

    private static final String QUESTION = "?";

    private static final String ASTERISK = "*";

    private static final String COMMA = ",";

    /**
     * 替换 分钟、小时、日期、星期
     */
    private static final String ORIGINAL_CRON = "0 %s %s %s * %s";

    private static final String ORIGINAL_CRON_SECOND = "%s %s %s %s * %s";

    /**
     * 执行一次确定时间的cron
     */
    private static final String ONCE_CRON = "0 %s %s %s %s ? %s ";

    private static final String ONCE_CRON_SECOND = "%s %s %s %s %s ? %s ";

    public static String onceExeTime(String cron) {
        Cron parse = parse(cron);
        try {
            ExecutionTime executionTime = ExecutionTime.forCron(parse);
            Optional<ZonedDateTime> zonedDateTime = executionTime.lastExecution(ZonedDateTime.now());
            if (zonedDateTime.isPresent()) {
                return zonedDateTime.get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } else {
                return executionTime.nextExecution(ZonedDateTime.now()).get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Cron parse(String cronString) {
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(QUARTZ);
        CronParser parser = new CronParser(cronDefinition);
        Cron quartzCron = parser.parse(cronString);
        return quartzCron;
    }

    public static void main(String[] args) {
        System.out.println(onceExeTime("12 59 15 24 4 ? 2021 "));
    }

    /**
     * 检查cron表达式的合法性
     */
    public boolean checkValid(String cron) {
        try {
            CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING);
            CronParser parser = new CronParser(cronDefinition);
            parser.parse(cron);
        } catch (IllegalArgumentException e) {
//            LOGGER.error(String.format("cron=%s not valid", cron));
            return false;
        }
        return true;
    }

    public String buildCron(List<Integer> minutes, List<Integer> hours, List<Integer> weekdays) {
        String minute;
        if (minutes.equals(this.getInitMinutes())) {
            minute = ASTERISK;
        } else {
            minute = StringUtils.join(minutes, COMMA);
        }
        String hour;
        if (hours.equals(this.getInitHours())) {
            hour = ASTERISK;
        } else {
            hour = StringUtils.join(hours, COMMA);
        }
        String weekday;
        if (weekdays.equals(this.getInitWeekdays())) {
            weekday = QUESTION;
        } else {
            weekday = StringUtils.join(weekdays, COMMA);
        }
        // 重点：星期和日字段冲突，判断周日的前端输入
        if (weekday.equals(QUESTION)) {
            return String.format(ORIGINAL_CRON, minute, hour, ASTERISK, weekday);
        } else {
            return String.format(ORIGINAL_CRON, minute, hour, QUESTION, weekday);
        }
    }

    public String buildCron(List<Integer> seconds, List<Integer> minutes, List<Integer> hours, List<Integer> weekdays) {
        String second;
        if (seconds.equals(this.getInitHours())) {
            second = ASTERISK;
        } else {
            second = StringUtils.join(seconds, COMMA);
        }

        String minute;
        if (minutes.equals(this.getInitMinutes())) {
            minute = ASTERISK;
        } else {
            minute = StringUtils.join(minutes, COMMA);
        }
        String hour;
        if (hours.equals(this.getInitHours())) {
            hour = ASTERISK;
        } else {
            hour = StringUtils.join(hours, COMMA);
        }
        String weekday;
        if (weekdays.equals(this.getInitWeekdays())) {
            weekday = QUESTION;
        } else {
            weekday = StringUtils.join(weekdays, COMMA);
        }
        // 重点：星期和日字段冲突，判断周日的前端输入
        if (weekday.equals(QUESTION)) {
            return String.format(ORIGINAL_CRON_SECOND, second, minute, hour, ASTERISK, weekday);
        } else {
            return String.format(ORIGINAL_CRON_SECOND, second, minute, hour, QUESTION, weekday);
        }
    }

    /**
     * 解析cron
     */
    public CustomCronField parseCon(String cron) {
        if (!this.checkValid(cron)) {
            return null;
        }
        List<String> result = Arrays.asList(cron.trim().split(" "));
        CustomCronField field = new CustomCronField();
        if (result.get(1).contains(COMMA)) {
            field.setMinutes(Arrays.stream(result.get(1).split(COMMA)).map(Integer::parseInt).collect(Collectors.toList()));
        } else if (result.get(1).equals(ASTERISK)) {
            field.setMinutes(this.getInitMinutes());
        } else {
            field.setMinutes(Lists.newArrayList(Integer.parseInt(result.get(1))));
        }
        if (result.get(2).contains(COMMA)) {
            field.setHours(Arrays.stream(result.get(2).split(COMMA)).map(Integer::parseInt).collect(Collectors.toList()));
        } else if (result.get(2).equals(ASTERISK)) {
            field.setHours(this.getInitHours());
        } else {
            field.setHours(Lists.newArrayList(Integer.parseInt(result.get(2))));
        }
        if (result.get(5).contains(COMMA)) {
            field.setWeekdays(Arrays.stream(result.get(5).split(COMMA)).map(Integer::parseInt).collect(Collectors.toList()));
        } else if (result.get(5).equals(QUESTION)) {
            field.setWeekdays(this.getInitWeekdays());
        } else {
            field.setWeekdays(Lists.newArrayList(Integer.parseInt(result.get(5))));
        }
        return field;
    }

    /**
     * 将Cron表达式解析为中文
     */
    public String translateToChinese(String cronExp) {
        if (cronExp == null || cronExp.length() < 1) {
            return "cron表达式为空";
        }

        String[] tmpCorns = cronExp.split(" ");
        StringBuffer sBuffer = new StringBuffer();
        if (tmpCorns.length == 6) {
            //解析月
            if (!tmpCorns[4].equals("*") && !tmpCorns[4].equals("?")) {
                if (tmpCorns[4].contains("/")) {
                    sBuffer.append("从").append(tmpCorns[4].split("/")[0]).append("号开始").append(",每").append
                            (tmpCorns[4].split("/")[1]).append("月");
                } else {
                    sBuffer.append("每年").append(tmpCorns[4]).append("月");
                }
            }

            //解析周
            if (!tmpCorns[5].equals("*") && !tmpCorns[5].equals("?")) {
                if (tmpCorns[5].contains(",")) {
                    sBuffer.append("每周").append(tmpCorns[5]).append("的");
                } else {
                    sBuffer.append("每周");
                    char[] tmpArray = tmpCorns[5].toCharArray();
                    for (char tmp : tmpArray) {
                        switch (tmp) {
                            case '1':
                                sBuffer.append("日");
                                break;
                            case '2':
                                sBuffer.append("一");
                                break;
                            case '3':
                                sBuffer.append("二");
                                break;
                            case '4':
                                sBuffer.append("三");
                                break;
                            case '5':
                                sBuffer.append("四");
                                break;
                            case '6':
                                sBuffer.append("五");
                                break;
                            case '7':
                                sBuffer.append("六");
                                break;
                            default:
                                sBuffer.append(tmp);
                                break;
                        }
                    }
                }
            }

            //解析日
            if (!tmpCorns[3].equals("?")) {
                if (sBuffer.toString().contains("一") && sBuffer.toString().contains("二") && sBuffer.toString()
                        .contains("三")
                        && sBuffer.toString().contains("四") && sBuffer.toString().contains("五") && sBuffer.toString()
                        .contains("六")
                        && sBuffer.toString().contains("日")) {
                }

                if (!tmpCorns[3].equals("*")) {
                    if (tmpCorns[3].contains("/")) {
                        sBuffer.append("每周从第").append(tmpCorns[3].split("/")[0]).append("天开始").append(",每").append
                                (tmpCorns[3].split("/")[1]).append("天");
                    } else {
                        sBuffer.append("每月第").append(tmpCorns[3]).append("天");
                    }
                }
            }

            //解析时
            if (!tmpCorns[2].equals("*")) {
                if (tmpCorns[2].contains("/")) {
                    sBuffer.append("从").append(tmpCorns[2].split("/")[0]).append("点开始").append(",每").append
                            (tmpCorns[2].split("/")[1]).append("小时");
                } else {
                    if (!(sBuffer.toString().length() > 0)) {
                        sBuffer.append("每天").append(tmpCorns[2]).append("点");
                    } else {
                        sBuffer.append(tmpCorns[2]).append("点");
                    }
                }
            }

            //解析分
            if (!tmpCorns[1].equals("*")) {
                if (tmpCorns[1].contains("/")) {
                    sBuffer.append("从第").append(tmpCorns[1].split("/")[0]).append("分开始").append(",每").append
                            (tmpCorns[1].split("/")[1]).append("分");
                } else if (tmpCorns[1].equals("0")) {

                } else {
                    sBuffer.append(tmpCorns[1]).append("分");
                }
            }
            if (sBuffer.toString().length() > 0) {
                sBuffer.append("执行一次");
            } else {
                sBuffer.append("表达式中文转换异常");
            }
        }
        return sBuffer.toString();
    }

    private List<Integer> initArray(Integer num) {
        List<Integer> result = Lists.newArrayListWithExpectedSize(num);
        for (int i = 0; i <= num; i++) {
            result.add(i);
        }
        return result;
    }

    public List<Integer> turnWeek(List<Integer> week) {
        List<Integer> integers = new ArrayList<>();
        for (Integer integer : week) {
            switch (integer) {
                case 1:
                    integers.add(2);
                    break;
                case 2:
                    integers.add(3);
                    break;
                case 3:
                    integers.add(4);
                    break;
                case 4:
                    integers.add(5);
                    break;
                case 5:
                    integers.add(6);
                    break;
                case 6:
                    integers.add(7);
                    break;
                case 7:
                    integers.add(1);
                    break;
                default:
                    break;
            }
        }
        return integers;
    }

    private List<Integer> getInitMinutes() {
        return this.initArray(59);
    }

    private List<Integer> getInitHours() {
        return this.initArray(23);
    }

    private List<Integer> getInitWeekdays() {
        return this.initArray(7).subList(1, 8);
    }

    public String buildCron(String year, String month, String day, String hour, String minute) {
        return String.format(ONCE_CRON, minute, hour, day, month, year);
    }

    public String buildCron(String year, String month, String day, String hour, String minute, String second) {
        return String.format(ONCE_CRON_SECOND, second, minute, hour, day, month, year);
    }

    private String getCron(String times, List<Integer> weeks) {
        if (FieldCheckUtil.checkStringNotEmpty(times) && FieldCheckUtil.checkListNotEmpty(weeks)) {
            String[] split = times.split(":");
            if (split != null && split.length > 1) {
                String minute = split[1];
                String hour = split[0];
                if (FieldCheckUtil.isInteger(minute) && FieldCheckUtil.isInteger(hour)) {
                    return buildCron(
                            Arrays.asList(Integer.valueOf(minute)),
                            Arrays.asList(Integer.valueOf(hour)),
                            weeks);
                } else {

                }
            }
        }
        return "";
    }

    private String getCron(String times, String ymd) {
        if (FieldCheckUtil.checkStringNotEmpty(times) && FieldCheckUtil.checkStringNotEmpty(ymd)) {
            String[] split = times.split(":");
            String[] ymds = ymd.split("-");
            if ((split != null && split.length > 1) && (ymds != null && ymds.length > 2)) {
                String minute = split[1];
                String hour = split[0];
                String year = ymds[0];
                String month = ymds[1];
                String day = ymds[2];

                if (FieldCheckUtil.isInteger(minute) &&
                        FieldCheckUtil.isInteger(hour) &&
                        FieldCheckUtil.isInteger(year) &&
                        FieldCheckUtil.isInteger(month) &&
                        FieldCheckUtil.isInteger(day)
                ) {
                    return buildCron(year, month, day, hour, minute);
                } else {

                }
            }
        }
        return "";
    }

    public static class CustomCronField {
        private List<Integer> minutes;
        private List<Integer> hours;
        private List<Integer> weekdays;
        
        public List<Integer> getMinutes() {
            return minutes;
        }
        
        public void setMinutes(List<Integer> minutes) {
            this.minutes = minutes;
        }
        
        public List<Integer> getHours() {
            return hours;
        }
        
        public void setHours(List<Integer> hours) {
            this.hours = hours;
        }
        
        public List<Integer> getWeekdays() {
            return weekdays;
        }
        
        public void setWeekdays(List<Integer> weekdays) {
            this.weekdays = weekdays;
        }
    }


}

