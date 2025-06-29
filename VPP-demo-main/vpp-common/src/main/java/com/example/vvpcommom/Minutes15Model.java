package com.example.vvpcommom;

import lombok.Data;

@Data
public class Minutes15Model {

    private String timeScope;

    private String endTime;

    private String startTime;
    
    // 显式添加setter方法，以防Lombok出现问题
    public void setTimeScope(String timeScope) {
        this.timeScope = timeScope;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
