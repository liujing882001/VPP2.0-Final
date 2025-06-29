package com.example.vvpdomain.entity;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@Entity
@Table(name = "online_rule")
@EntityListeners(AuditingEntityListener.class)
public class OnlineRule {

	@Id
	@Column(name = "node_id")
	private String nodeId;

	@Column(name = "device_list")
	private String deviceList;
}
