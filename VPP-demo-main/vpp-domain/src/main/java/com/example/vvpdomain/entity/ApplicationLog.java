package com.example.vvpdomain.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "application_log")
public class ApplicationLog {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "application_id")
	private String applicationId;

	@Column(name = "application_name")
	private String applicationName;

	@Column(name = "ts")
	private LocalDateTime ts;

	public ApplicationLog(){
		this.id = UUID.randomUUID().toString();
		this.ts = LocalDateTime.now();
	}
}
