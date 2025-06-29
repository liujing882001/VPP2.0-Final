package com.example.vvpdomain.entity;


import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author konghao
 * @description 负荷调节
 * @date 2023-03-07
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "demand_load_regulation")
public class DemandLoadRegulation {

    @Id
    @Column(name = "id")
    private String id;
    /**
     * 任务id
     */
    @Column(name = "resp_id")
    private String respId;
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
     * 负荷调节值
     */
    @Column(name = "load_regulation_value")
    private String loadRegulationValue;
    /**
     * 时间点
     */
    @Column(name = "regulation_time")
    private Date regulationTime;

}
