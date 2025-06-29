package com.example.vvpweb.demand.model;

import lombok.Data;

import java.util.List;

@Data
public class DrsRCommand {
    private String root;
    private String version;
    private String requestID;
    private String createdDateTime;
    private List<DrsRPointData> pointData;
}
