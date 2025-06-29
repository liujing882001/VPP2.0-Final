package com.example.vvpweb.loadmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class EnergyModel implements Serializable {

    private List<String> nodeIds;

    private List<SubModel> subModelList;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date ts;

}
