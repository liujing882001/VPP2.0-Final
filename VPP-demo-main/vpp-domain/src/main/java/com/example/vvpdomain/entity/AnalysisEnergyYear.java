package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zph
 * @description analysis_energy_year_view视图
 * @date 2022-07-05
 */
@Entity
@Getter
@Table(name = "analysis_energy_year_view")
@EntityListeners(AuditingEntityListener.class)
public class AnalysisEnergyYear implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * energy_id
     */
    @Column(name = "energy_id")
    private String energyId;

    /**
     * energy_value
     */
    @Column(name = "energy_value")
    private String energyValue;

    /**
     * 节点id
     */
    @Column(name = "node_id")
    private String nodeId;
    /**
     * node_name
     */
    @Column(name = "node_name")
    private String nodeName;

    /**
     * 系统类型名称
     */
    @Column(name = "system_name")
    private String systemName;
    /**
     * system_id
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * 类型 load,energy
     */
    @Column(name = "point_desc")
    private String pointDesc;
    /**
     * 时间 年月
     */
    @Column(name = "ts")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date ts;


    public AnalysisEnergyYear() {
    }

}