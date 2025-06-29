package com.example.vvpweb.tradepower.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class DispatchCurveRes {
    @ApiModelProperty("字段名称")
    private String name;

    @ApiModelProperty("数据")
    private List<DispatchCurveDateRes> dataList;

    @ApiModelProperty("是否展示")
    private Boolean show;

    public DispatchCurveRes(String name, Boolean show, List<StrategyTimeModel> dataList, String date) {
        this.name = name;
        this.dataList = toVo(dataList,date);
        this.show = show;
    }
    public DispatchCurveRes(String name, Boolean show,List<DispatchCurveDateRes> dataList) {
        this.name = name;
        this.dataList = dataList;
        this.show = show;
    }
    private List<DispatchCurveDateRes> toVo(List<StrategyTimeModel> dataList, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");

        List<DispatchCurveDateRes> list = new ArrayList<>();
        dataList.forEach(v -> {
            try {
                Date date1 = sdf.parse(date + " "+ sdf1.format(v.getStime()) +":00");
                DispatchCurveDateRes data = new DispatchCurveDateRes(date1,v.getPower());
                list.add(data);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        return list;
    }
}
