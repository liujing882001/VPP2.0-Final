package com.example.vvpcommom;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimestampSlicing implements Serializable {


    public static List<Date> time96Points() {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        try {
            Date startDate = sdf.parse("00:00");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            List<Date> timePoints = new ArrayList<>();

            for (int i = 0; i < 96; i++) {
                // 设置时间部分
                calendar.set(Calendar.HOUR_OF_DAY, i / 4); // 4 time points per hour
                calendar.set(Calendar.MINUTE, (i % 4) * 15); // 15 minutes per time point
                timePoints.add(calendar.getTime());
            }

            return timePoints;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getRangeIndex(List<Date> timePoints, Date inputTime) {
        for (int i = 0; i < timePoints.size(); i++) {
            if (i < timePoints.size() - 1) {
                if (inputTime.after(timePoints.get(i)) && inputTime.before(timePoints.get(i + 1))) {
                    return i;
                }
            }
        }
        return -1;
    }


}
