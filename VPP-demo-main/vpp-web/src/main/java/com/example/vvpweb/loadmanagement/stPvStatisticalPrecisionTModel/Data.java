/**
 * Copyright 2023 json.cn
 */
package com.example.vvpweb.loadmanagement.stPvStatisticalPrecisionTModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class Data implements Serializable {

    private List<Stat_infos> stat_infos = new ArrayList<>();
    private int total;
    private int page;
    private int page_size;
}