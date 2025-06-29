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
 * @description 光伏-电力用户购电比例
 * @date 2022-07-29
 */
@Entity
@Getter
@Setter
@Table(name = "cfg_photovoltaic_discount_rate")
@EntityListeners(AuditingEntityListener.class)
public class CfgPhotovoltaicDiscountRate implements Serializable {

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
     * 年序号
     */
    @Column(name = "`order`")
    private int order;
    /**
     * 电力用户比例
     */
    @Column(name = "power_user_prop")
    private Double powerUserProp;

    /**
     * 资产方比例
     */
    @Column(name = "load_prop")
    private Double loadProp;

    /**
     * 运营方比例
     */
    @Column(name = "operator_prop")
    private Double operatorProp;

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

    public CfgPhotovoltaicDiscountRate() {
    }

}