package com.example.vvpscheduling.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandProfitRevertModel implements Serializable {

    private String node_id;
    private Integer resp_type;
    private Double real_time_load;
    private Double declare_load;
    private Double profit;
    private Double volume_profit;
}
