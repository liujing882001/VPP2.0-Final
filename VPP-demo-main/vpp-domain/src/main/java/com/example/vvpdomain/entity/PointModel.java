package com.example.vvpdomain.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
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

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getKey() { return key; }
	public void setKey(String key) { this.key = key; }
	public String getPointType() { return pointType; }
	public void setPointType(String pointType) { this.pointType = pointType; }
	public String getPointDesc() { return pointDesc; }
	public void setPointDesc(String pointDesc) { this.pointDesc = pointDesc; }
	public String getPointNameZh() { return pointNameZh; }
	public void setPointNameZh(String pointNameZh) { this.pointNameZh = pointNameZh; }
	public String getPointNameEn() { return pointNameEn; }
	public void setPointNameEn(String pointNameEn) { this.pointNameEn = pointNameEn; }
	public String getUnit() { return unit; }
	public void setUnit(String unit) { this.unit = unit; }
	public String getGranularity() { return granularity; }
	public void setGranularity(String granularity) { this.granularity = granularity; }
	public String getSource() { return source; }
	public void setSource(String source) { this.source = source; }
	public String getUsage() { return usage; }
	public void setUsage(String usage) { this.usage = usage; }
	public String getRemark() { return remark; }
	public void setRemark(String remark) { this.remark = remark; }

}
