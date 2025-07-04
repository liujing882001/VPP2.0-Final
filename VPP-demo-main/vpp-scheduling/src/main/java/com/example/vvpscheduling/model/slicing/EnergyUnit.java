package com.example.vvpscheduling.model.slicing;

import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.view.IotTsKvEnergyLastWeekView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Data
@Getter
@Setter
public class EnergyUnit{
    private IotTsKvEnergyLastWeekView left;
    private IotTsKvEnergyLastWeekView right;
    private String dataStr;


    public double calculate(){
        long minutesBetween = ChronoUnit.MINUTES.between(LocalDateTime.ofInstant(Instant.ofEpochMilli(getLeft().getTs().getTime()), ZoneOffset.of("+8")),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(getRight().getTs().getTime()), ZoneOffset.of("+8")));

        if(minutesBetween != 0){
            double slope = (Double.valueOf(getRight().getPointValue()) - Double.valueOf(getLeft().getPointValue())) / minutesBetween;
            long minutesFromLeft = ChronoUnit.MINUTES.between(LocalDateTime.ofInstant(Instant.ofEpochMilli(getLeft().getTs().getTime()), ZoneOffset.of("+8")), LocalDateTime.ofInstant(Instant.ofEpochMilli(TimeUtil.stringToDate(getDataStr()).getTime()), ZoneOffset.of("+8")));
            double kwh = Double.valueOf(getLeft().getPointValue()) + slope * minutesFromLeft;
            return kwh;
        }
        return Double.valueOf(getRight().getPointValue());
    }

    // Manual getters to ensure compilation
    public IotTsKvEnergyLastWeekView getLeft() { return left; }
    public IotTsKvEnergyLastWeekView getRight() { return right; }
    public String getDataStr() { return dataStr; }

}

