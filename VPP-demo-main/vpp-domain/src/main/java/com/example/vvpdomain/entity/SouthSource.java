package com.example.vvpdomain.entity;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "station_node")
public class SouthSource implements Serializable {
    @Id
    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "resource_id")
    private String resourceId;

    @Column(name = "station_id")
    private String stationId;
}
