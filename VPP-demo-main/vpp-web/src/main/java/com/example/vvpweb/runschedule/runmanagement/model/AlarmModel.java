package com.example.vvpweb.runschedule.runmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@AllArgsConstructor
public class AlarmModel implements Serializable {
    Integer severity;
    BigInteger count;

    public AlarmModel() {
    }

}
