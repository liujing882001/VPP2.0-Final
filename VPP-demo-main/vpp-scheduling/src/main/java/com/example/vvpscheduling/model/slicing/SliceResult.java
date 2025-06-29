package com.example.vvpscheduling.model.slicing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Getter
@Setter
public  class SliceResult {
    double max;
    double min;
    double avg;
    double firstValue;
    double lastValue;
    Date lastTimestamp;
    Date countDataTime;
    int sliceIndex;
    String timeScope;
    boolean sliceSupplementRecording;

    @Override
    public String toString() {
        return "SliceResult{" +
                "max=" + max +
                ", min=" + min +
                ", avg=" + avg +
                ", firstValue=" + firstValue +
                ", lastValue=" + lastValue +
                ", lastTimestamp=" + lastTimestamp +
                ", countDataTime=" + countDataTime +
                ", sliceIndex=" + sliceIndex +
                ", timeScope=" + timeScope +
                ", sliceSupplementRecording=" + sliceSupplementRecording +
                '}';
    }
}
