package com.example.vvpweb.runschedule.runmanagement.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class StorageEnergyResponse implements Serializable {
    List<String> y = new ArrayList<>();
    List<Double> x_in = new ArrayList<>();
    List<Double> x_out = new ArrayList<>();
}
