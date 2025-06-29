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
	}
}
