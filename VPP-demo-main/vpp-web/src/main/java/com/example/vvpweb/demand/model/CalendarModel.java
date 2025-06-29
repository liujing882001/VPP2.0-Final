package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CalendarModel {

    @ApiModelProperty("日期列表")
    private List<Date> dateList;

    @ApiModelProperty("日期类型（1-工作日 2-非工作日 3-删除计算日 4-元旦 5-春节 6-清明节 7-劳动节 8-端午节 9-中秋节 10-国庆节）")
    private Integer dateType;

}
