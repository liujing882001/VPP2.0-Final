package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zph
 * @description 光伏基本信息配置
 * @date 2022-07-24
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cfg_photovoltaic_base_info")
public class CfgPhotovoltaicBaseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 参数主键=键名类型父菜单id
     */
    @Column(name = "id")
    private String id;

    /**
     * 参数名称
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 参数键名
     */
    @Column(name = "system_id")
    private String systemId;
    /**
     * 数据类型（pv_baseInfo，timeDivision，powerUser）
     */
    @Column(name = "data_type")
    private String dataType;

    /**
     * 光伏装机容量 kwp
     */
    @Column(name = "photovoltaic_installed_capacity")
    private double photovoltaicInstalledCapacity;


    /**
     * 分时电价开始时间 yyyy-MM
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    @Column(name = "time_division_start_time")
    private Date timeDivisionStartTime;
    /**
     * 分时电价年限
     */
    @Column(name = "time_division_expiry_date")
    private int timeDivisionExpiryDate;


    /**
     * 电力用户购电折扣比例 开始时间 yyyy-MM
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    @Column(name = "power_user_start_time")
    private Date powerUserStartTime;
    /**
     * 电力用户购电折扣比例 年限
     */
    @Column(name = "power_user_expiry_date")
    private int powerUserExpiryDate;

    /**
     * created_time
     */
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public CfgPhotovoltaicBaseInfo() {
    }

}