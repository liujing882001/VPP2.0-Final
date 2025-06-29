package com.example.vvpdomain.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sales_per_squaremeter_governmentofficelessthan20000_view")
public class SalesPerSquaremeterGovernmentOfficeLessThan20000View {

    @Id

    /**
     * id
     */
    @Column(name = "id")
    private String id;

    @Column(name = "h_total_use")
    private Double hTotalUse;


    @Column(name = "node_area")
    private Double nodeArea;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Column(name = "count_date")
    private Date countDate;

}


