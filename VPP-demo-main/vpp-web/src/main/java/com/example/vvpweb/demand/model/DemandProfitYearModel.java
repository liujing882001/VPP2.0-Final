package com.example.vvpweb.demand.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandProfitYearModel implements Serializable {

    private String node_id;
    private String node_name;
    private Integer profit_year;
    private Double profits;


}
