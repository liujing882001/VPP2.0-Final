package com.example.vvpweb.systemmanagement.stationnode.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
public class StationPageQueryCommand {
    /**
     * 页数
     */
    @NotBlank(message = "页数不能为空")
    private int page;

    /**
     * 个数
     */
    @NotBlank(message = "个数不能为空")
    private int size;

    private String query;


    private Map<String,Object> keyword;
    public List<String> getStationTypeIds() {
        return (List<String>) keyword.get("stationTypeId");
    }

    public List<String> getStationState() {
        return (List<String>) keyword.get("stationState");
    }

    public String getStationCategory() {
        return (String) keyword.get("stationCategory");
    }
}
