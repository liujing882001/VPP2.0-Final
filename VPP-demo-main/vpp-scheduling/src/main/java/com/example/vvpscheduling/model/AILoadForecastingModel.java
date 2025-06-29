package com.example.vvpscheduling.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AILoadForecastingModel {

    private String node_id;
    private Double longitude;
    private Double latitude;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date count_date;
    private String id;
    private Double h_total_use;

}
