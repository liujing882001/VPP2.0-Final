package com.example.vvpweb.loadmanagement.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class EnergyResourceModel implements Serializable {

    private String total;

    private List<Map<String, Object>> data = new ArrayList<>();
}
