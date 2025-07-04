package com.example.vvpdomain.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "electricity_holiday")
public class ElectricityHolidayInfo {

	@EmbeddedId
	private ElectricityHolidayKey pk = new ElectricityHolidayKey();

	@Column(name = "type")
	private String type;

	@Column(name = "price")
	private BigDecimal price;

	@Data
	@Embeddable
	public static class ElectricityHolidayKey implements Serializable {
		@Column(name = "node_id")
		private String nodeId;

		@Column(name = "date")
		private LocalDate date;

		@Column(name = "st")
		private LocalTime st;

		@Column(name = "et")
		private LocalTime et;

		public String getNodeId() { return nodeId; }
		public void setNodeId(String nodeId) { this.nodeId = nodeId; }
		public java.time.LocalDate getDate() { return date; }
		public void setDate(java.time.LocalDate date) { this.date = date; }
		public java.time.LocalTime getSt() { return st; }
		public void setSt(java.time.LocalTime st) { this.st = st; }
		public java.time.LocalTime getEt() { return et; }
		public void setEt(java.time.LocalTime et) { this.et = et; }
	}

	public ElectricityHolidayKey getPk() { return pk; }
	public java.math.BigDecimal getPrice() { return price; }
	public String getType() { return type; }

	// Manual setters to ensure compilation
	public void setType(String type) { this.type = type; }
	public void setPrice(BigDecimal price) { this.price = price; }
}
