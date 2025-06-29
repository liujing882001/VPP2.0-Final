package com.example.vvpweb.demand.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandProfitDayModel implements Serializable {

    private String node_id;
    private String node_name;
    private Date profit_date;
    private Double profits;


}
