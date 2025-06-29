package com.example.vvpweb.Intelligentcabinet.model;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.TimeUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UpsStatusResponse implements Serializable {

    @ApiModelProperty("最新状态，时间格式为yyyy-MM-dd HH:mm:ss")
    private String timestamp;

    //battery_current
    @ApiModelProperty("蓄电池电流0.1A")
    private String batteryCurrent;
    //line_voltage
    @ApiModelProperty("线路电压0.1V")
    private String lineVoltage;
    //input_frequency
    @ApiModelProperty("输入频率0.1Hz")
    private String inputfrequency;
    //output_frequency
    @ApiModelProperty("输出频率0.01Hz")
    private String outputfrequency;
    //output_voltage
    @ApiModelProperty("输出电压0.1V")
    private String outputVoltage;
    //output_current
    @ApiModelProperty("输出电流0.1A")
    private String outputCurrent;
    //output_load_percent
    @ApiModelProperty("输出负载百分比0.1%")
    private String outputLoadPercent;
    //battery_voltage
    @ApiModelProperty("蓄电池电压0.1V")
    private String batteryVoltage;
    //temperature
    @ApiModelProperty("UPS内部温度0.1℃")
    private String temperature;
    //status
    @ApiModelProperty("Ups状态")
    private String status;
    //battery_capacity
    @ApiModelProperty("电池容量")
    private String batteryCapacity;
    

    public static List<UpsStatusResponse> mockResponse(Date startTime) {
        String jsonData = "[{\n"+
                "\t\t\"timestamp\": \"2010-3-11 0:00:00\",\n"+
                "\t\t\"batteryCurrent\": \"0.5\",\n"+
                "\t\t\"lineVoltage\": \"145.45\",\n"+
                "\t\t\"temperature\": \"21.25428772\",\n"+
                "\t\t\"inputfrequency\": \"43.5815506\",\n"+
                "\t\t\"outputfrequency\": \"2.227393627\",\n"+
                "\t\t\"outputVoltage\": \"0.255153656\",\n"+
                "\t\t\"outputCurrent\": \"100.2713699\",\n"+
                "\t\t\"outputLoadPercent\": \"87\",\n"+
                "\t\t\"batteryVoltage\": \"198\",\n"+
                "\"batteryCapacity\": \"67\",\n"+
                "\"status\": \"1\"\n"+
                "}]\n";
        List<UpsStatusResponse> upsStatusResponses = JSONObject.parseArray(jsonData, UpsStatusResponse.class);
        upsStatusResponses.get(0).setTimestamp(TimeUtil.dateFormat(startTime));
        return upsStatusResponses;
    }


}
