package com.example.vvpweb.profitmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class ProfitMonthRequest {
    /**
     * 节点id
     */
    private List<String> nodeId;

    /**
     * 开始时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date startTs;

    /**
     * 结束时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date endTs;


    private ProfitUserType userType;


    public List<String> getNodeId() {
        return nodeId;
    }

    public void setNodeId(List<String> nodeId) {
        this.nodeId = nodeId;
    }

    public Date getStartTs() {
        return startTs;
    }

    public void setStartTs(Date startTs) {
        this.startTs = startTs;
    }

    public Date getEndTs() {
        return endTs;
    }

    public void setEndTs(Date endTs) {
        this.endTs = endTs;
    }

    public ProfitUserType getUserType() {
        return userType;
    }

    public void setUserType(ProfitUserType userType) {
        this.userType = userType;
    }
}
