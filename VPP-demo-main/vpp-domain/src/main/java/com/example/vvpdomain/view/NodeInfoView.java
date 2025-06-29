package com.example.vvpdomain.view;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author zph
 * @description 节点基本信息视图
 * @date 2022-08-10
 */
@Entity
@Getter
@Table(name = "node_info_view")
@EntityListeners(AuditingEntityListener.class)
public class NodeInfoView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * id
     */
    @Column(name = "id")
    private String id;
    /**
     * 省所在区域id
     */
    @Column(name = "province_region_id")
    private String provinceRegionId;

    /**
     * 省所在区域名称
     */
    @Column(name = "province_region_name")
    private String provinceRegionName;


    /**
     * 市所在区域id
     */
    @Column(name = "city_region_id")
    private String cityRegionId;

    /**
     * 市所在区域名称
     */
    @Column(name = "city_region_name")
    private String cityRegionName;

    /**
     * 县/区所在区域id
     */
    @Column(name = "county_region_id")
    private String countyRegionId;

    /**
     * 县/区所在区域名称
     */
    @Column(name = "county_region_name")
    private String countyRegionName;

    /**
     * 电站名称id
     */
    @Column(name = "node_id")
    private String nodeId;
    /**
     * 电站名称
     */
    @Column(name = "node_name")
    private String nodeName;

    /**
     * node_post_type
     * 节点归属类型 load 负荷，pv 光伏，storageEnergy 储能
     */
    @Column(name = "node_post_type")
    private String nodePostType;

    /**
     * 经度
     */
    @Column(name = "longitude")
    private double longitude;

    /**
     * 纬度
     */
    @Column(name = "latitude")
    private double latitude;


    /**
     * 建设中/已完成，默认false 为建设中
     */
    @Column(name = "is_enabled")
    private Boolean isEnabled;


    /**
     * 电站状态
     */
    @Column(name = "`online`")
    private Boolean online;

    /**
     * 光伏装机容量/储能电池容量/可调负荷：负荷节点所有系统中设备的额定功率之和
     */
    @Column(name = "capacity")
    private Double capacity;


    public NodeInfoView() {
    }

}