package com.example.vvpweb.loadmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LoadYearModel implements Serializable {

    private List<String> nodeIds;

    private List<SubModel> subModelList;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy")
    private int ts;

}
