package com.example.vvpdomain.entity;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Revenue Parameter Entity
 * Represents configuration parameters for revenue estimation
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "revenue_estimation_parameter")
public class RevenueParameterDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "param_id")
	private String paramId;

	@Column(name = "param_name")
	private String paramName;

	@Column(name = "default_value")
	private String defaultValue;

	@Column(name = "category")
	private String category;

	@Column(name = "unit")
	private String unit;

	@Column(name = "is_variable")
	private Boolean isVariable;

	/**
	 * Default constructor
	 */
	public RevenueParameterDto() {
	}
}
