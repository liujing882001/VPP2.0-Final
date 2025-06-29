package com.example.vvpweb.runschedule.runmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastSERInfoModel implements Serializable {

    private String node_id;
    private String system_id;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp count_data_time;
    private Double total_power_energy;
    private Double energy;
    private Double load;
    private Double soh;
    private Double accumulated_startup_time;
    private Double soc;
}
