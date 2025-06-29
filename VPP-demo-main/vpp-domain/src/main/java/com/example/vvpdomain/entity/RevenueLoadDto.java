package com.example.vvpdomain.entity;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "revenue_load_data")
public class RevenueLoadDto implements Serializable {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "project_id")
	private String projectId;

	@Column(name = "time")
	private Date time;

	@Column(name = "power")
	private Double power;
}
