package com.example.vvpweb.tradepower.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OperationModel {
    private String nodeId;
    private String nodeName;
    private List<OperationTimeModel> list;
}
