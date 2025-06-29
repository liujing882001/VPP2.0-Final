package com.example.vvpdomain.entity;


import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Table(name = "point_model")
@EntityListeners(AuditingEntityListener.class)
public class PointModel implements Serializable {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "key")
	private String key;

	@Column(name = "point_type")
	private String pointType;

	@Column(name = "point_desc")
	private String pointDesc;

	@Column(name = "point_name_zh")
	private String pointNameZh;

	@Column(name = "point_name_en")
	private String pointNameEn;

	@Column(name = "unit")
	private String unit;

	@Column(name = "granularity")
	private String granularity;

	@Column(name = "source")
	private String source;

	@Column(name = "usage")
	private String usage;

	@Column(name = "remark")
	private String remark;

}
