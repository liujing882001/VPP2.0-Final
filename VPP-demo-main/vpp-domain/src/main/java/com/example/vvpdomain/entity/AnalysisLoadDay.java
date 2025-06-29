package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zph
 * @description analysis_load_day_view视图
 * @date 2022-07-05
 */
@Entity
@Getter
@Table(name = "analysis_load_day_view")
@EntityListeners(AuditingEntityListener.class)
public class AnalysisLoadDay implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * load_id
     */
    @Column(name = "load_id")
    private String loadId;

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
     * 系统id
     */
    @Column(name = "system_id")
    private String systemId;


    /**
     * load_value
     */
    @Column(name = "load_value")
    private String loadValue;

    /**
     * 类型 load,energy
     */
    @Column(name = "point_desc")
    private String pointDesc;
    /**
     * 时间 年月日 时分秒
     */
    @Column(name = "ts")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ts;

    /**
     * 时间年月日
     */
    @Column(name = "ymd_ts")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date ymdTs;


    public AnalysisLoadDay() {
    }

}