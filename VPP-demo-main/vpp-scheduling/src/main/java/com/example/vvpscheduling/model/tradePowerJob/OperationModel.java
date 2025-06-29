package com.example.vvpscheduling.model.tradePowerJob;

import lombok.Data;

import java.util.List;

@Data
public class OperationModel {
    private String nodeId;
    private String nodeName;
    private List<OperationTimeModel> list;
}
