package com.example.vvpweb.systemmanagement.nodemodel.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegionInfoResponse implements Serializable {

    private String regionId;

    /**
     * "地区名称"
     */

    private String regionName;

    /**
     * "地区缩写"
     */
    private String regionShortName;

    /**
     * "行政地区编号"
     */
    private String regionCode;

}
