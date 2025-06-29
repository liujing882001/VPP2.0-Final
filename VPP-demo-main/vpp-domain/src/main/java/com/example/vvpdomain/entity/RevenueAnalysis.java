package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Revenue Analysis Entity
 * Represents revenue analysis data for stations
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "revenue_analysis")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "stationId")
public class RevenueAnalysis implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "station_id")
    private String stationId;

    @Column(name = "mouth_revenue", precision = 20, scale = 2)
    private BigDecimal monthRevenue;

    @Column(name = "mouth_count")
    private Integer monthCount;

    @Column(name = "year_revenue", precision = 20, scale = 2)
    private BigDecimal yearRevenue;

    @Column(name = "year_count")
    private Integer yearCount;

    /**
     * Default constructor
     */
    public RevenueAnalysis() {
    }
}
