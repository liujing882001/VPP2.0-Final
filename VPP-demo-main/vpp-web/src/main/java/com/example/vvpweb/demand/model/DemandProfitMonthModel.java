package com.example.vvpweb.demand.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandProfitMonthModel implements Serializable {

    private String node_id;
    private String node_name;
    private Integer profit_year_month;
    private String profit_year_month_str;
    private Double profits;


}
