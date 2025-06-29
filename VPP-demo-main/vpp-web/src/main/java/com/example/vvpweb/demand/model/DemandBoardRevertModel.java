package com.example.vvpweb.demand.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandBoardRevertModel implements Serializable {

    private Integer resp_type;
    private Double real_time_load;
    private Double declare_load;
    private Double profit;
    private Double volume_profit;
}
