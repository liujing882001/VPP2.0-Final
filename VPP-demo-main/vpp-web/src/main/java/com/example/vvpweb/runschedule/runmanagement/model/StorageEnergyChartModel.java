package com.example.vvpweb.runschedule.runmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageEnergyChartModel implements Serializable {

    private String ts;
    private Double h_total_use;
}
