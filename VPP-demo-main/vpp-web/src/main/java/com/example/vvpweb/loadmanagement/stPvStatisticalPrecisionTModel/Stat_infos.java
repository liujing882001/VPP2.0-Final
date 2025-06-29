/**
 * Copyright 2023 json.cn
 */
package com.example.vvpweb.loadmanagement.stPvStatisticalPrecisionTModel;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

@lombok.Data
public class Stat_infos implements Serializable {

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date date;
    private double accurate;
}