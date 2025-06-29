package com.example.vvpweb.demand.model;

import lombok.Data;

@Data
public class DrsResponse {
    private String root;
    private Integer version;
    private Integer code;
    private String description;
    private String requestID;
    private String token;
}
