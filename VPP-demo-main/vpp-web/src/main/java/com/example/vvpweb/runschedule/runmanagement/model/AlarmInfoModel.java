package com.example.vvpweb.runschedule.runmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@AllArgsConstructor
public class AlarmInfoModel implements Serializable {
    String system_name;
    BigInteger count;

    public AlarmInfoModel() {
    }

}
