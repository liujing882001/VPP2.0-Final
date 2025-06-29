package com.example.vvpweb.Intelligentcabinet.model;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.TimeUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class RetrieveResponse implements Serializable {
    @ApiModelProperty("格式为日或月")
    private String time;

    @ApiModelProperty("实际收益/预测收益")
    private String name;

    @ApiModelProperty("实际收益值/预测收益值")
    private String value;

    public static List<RetrieveResponse> mockResponse(int timeType){
        String jsonData ="[{\n" +
                "\t\t\"time\": \"1\",\n" +
                "\t\t\"name\": \"预测收益\",\n" +
                "\t\t\"value\": \"4.720274706\"\n" +
                "\t},{\n" +
                "\t\t\"time\": \"1\",\n" +
                "\t\t\"name\": \"实际收益\",\n" +
                "\t\t\"value\": \"4.520274706\"\n" +
                "\t}\n" +
                "]\n";


        List<RetrieveResponse> retrieveResponses = JSONObject.parseArray(jsonData, RetrieveResponse.class);
        retrieveResponses.forEach(retrieveResponse->{
            retrieveResponse.setTime(timeType+"");
            if(timeType==1){
                retrieveResponse.setValue(Double.parseDouble(retrieveResponse.getValue())+123.45d+"");
            }
        });

        return retrieveResponses;

    }
}
