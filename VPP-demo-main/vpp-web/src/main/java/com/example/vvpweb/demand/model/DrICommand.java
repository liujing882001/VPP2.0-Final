package com.example.vvpweb.demand.model;

import lombok.Data;

import java.util.List;

@Data
public class DrICommand {
    private String root;
    private Integer version;
    private String requestID;
    private List<DrIInviteData> inviteData;
}
