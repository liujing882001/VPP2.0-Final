package com.example.vvpscheduling.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Zhaoph
 */
@Data
public class SchedulingBean implements Serializable {

    private List<Float> time_prices;
    private int time_quota;
}
