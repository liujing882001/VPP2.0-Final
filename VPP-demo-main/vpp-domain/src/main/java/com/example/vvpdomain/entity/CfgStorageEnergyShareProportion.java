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
 * @description 参数配置表-逻辑中需根据模块id和配置代码查询配置项,根据不同的配置值做出相应的处理.
 * @date 2022-07-27
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cfg_storage_energy_share_proportion")
public class CfgStorageEnergyShareProportion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 参数主键=键名类型父菜单id
     */
    @Column(name = "id")
    private String id;

    /**
     * 节点id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 系统id
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * 年序号
     */
    @Column(name = "`order`")
    private Integer order;

    /**
     * 负荷集成商比例
     */
    @Column(name = "load_prop")
    private Double loadProp;

    /**
     * 电力用户比例
     */
    @Column(name = "power_user_prop")
    private Double powerUserProp;

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

    public CfgStorageEnergyShareProportion() {
    }

}