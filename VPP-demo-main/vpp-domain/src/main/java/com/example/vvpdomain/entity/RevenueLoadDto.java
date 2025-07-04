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

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getProjectId() { return projectId; }
	public void setProjectId(String projectId) { this.projectId = projectId; }
	public Date getTime() { return time; }
	public void setTime(Date time) { this.time = time; }
	public Double getPower() { return power; }
	public void setPower(Double power) { this.power = power; }
}
