package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "compute_node")
public class ComputeNode implements Serializable {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "resource_id")
    private String resourceID;
    @Column(name = "timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    @Column(name = "value_type")
    private Integer valueType;
    @Column(name = "value")
    private Double value;
    @Column(name = "data_type")
    private String dataType;
    @Column(name = "period")
    private String period;
    @Column(name = "up_time")
    private LocalDateTime upTime;
}
