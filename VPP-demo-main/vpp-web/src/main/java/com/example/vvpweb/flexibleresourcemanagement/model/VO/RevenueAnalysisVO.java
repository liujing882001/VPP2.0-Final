package com.example.vvpweb.flexibleresourcemanagement.model.VO;

import lombok.Data;

import java.util.List;

@Data
public class RevenueAnalysisVO {
    private List<RAInfoVO> yield;
    private List<RAInfoVO> cycleCount;
}
