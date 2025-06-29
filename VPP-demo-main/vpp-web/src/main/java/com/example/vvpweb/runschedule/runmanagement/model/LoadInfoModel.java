package com.example.vvpweb.runschedule.runmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class LoadInfoModel implements Serializable {

    String system_id;
    String point_value;

    public LoadInfoModel() {
    }
}
