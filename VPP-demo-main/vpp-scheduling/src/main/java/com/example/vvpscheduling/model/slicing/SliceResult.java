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

    // Manual getters and setters to ensure compilation
    public double getMax() { return max; }
    public void setMax(double max) { this.max = max; }
    public void setMax(Double max) { this.max = max != null ? max : 0.0; }
    public double getMin() { return min; }
    public void setMin(double min) { this.min = min; }
    public void setMin(Double min) { this.min = min != null ? min : 0.0; }
    public double getAvg() { return avg; }
    public void setAvg(double avg) { this.avg = avg; }
    public void setAvg(Double avg) { this.avg = avg != null ? avg : 0.0; }
    public double getFirstValue() { return firstValue; }
    public void setFirstValue(double firstValue) { this.firstValue = firstValue; }
    public double getLastValue() { return lastValue; }
    public void setLastValue(double lastValue) { this.lastValue = lastValue; }
    public Date getLastTimestamp() { return lastTimestamp; }
    public void setLastTimestamp(Date lastTimestamp) { this.lastTimestamp = lastTimestamp; }
    public Date getCountDataTime() { return countDataTime; }
    public void setCountDataTime(Date countDataTime) { this.countDataTime = countDataTime; }
    public int getSliceIndex() { return sliceIndex; }
    public void setSliceIndex(int sliceIndex) { this.sliceIndex = sliceIndex; }
    public String getTimeScope() { return timeScope; }
    public void setTimeScope(String timeScope) { this.timeScope = timeScope; }
    public boolean isSliceSupplementRecording() { return sliceSupplementRecording; }
    public void setSliceSupplementRecording(boolean sliceSupplementRecording) { this.sliceSupplementRecording = sliceSupplementRecording; }
}
