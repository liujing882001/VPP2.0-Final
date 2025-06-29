package com.example.vvpscheduling.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIPVForecastingModel {

    private String node_id;
    private String dt;
    private String id;
    private Double h_total_use;

}
