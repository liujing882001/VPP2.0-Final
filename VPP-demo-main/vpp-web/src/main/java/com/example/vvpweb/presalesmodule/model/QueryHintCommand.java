package com.example.vvpweb.presalesmodule.model;

import lombok.Data;

@Data
public class QueryHintCommand {
    private String sessionId;
    private String requestId;
    private String system;
    private String query;
    private Integer queryCount;

}
