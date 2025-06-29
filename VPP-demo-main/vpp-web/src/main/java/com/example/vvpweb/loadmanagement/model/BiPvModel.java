package com.example.vvpweb.loadmanagement.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class BiPvModel implements Serializable {


    /**
     * 类型 0 全部  1 正常  2 离线 3 建设中
     */
    private int type;


    private String stationName;

    /**
     * 每页大小
     */
    private int pageSize;
    /**
     * 当前页为第几页 默认 1开始
     */
    private int number;
}
