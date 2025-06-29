package com.example.vvpweb.alarmmanagement;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HikariCPMonitorController {

	@Autowired
	private HikariDataSource dataSource;

	@GetMapping("/hikari/status")
	public String getHikariStatus() {
		return String.format("Active Connections: %d, Idle Connections: %d, Total Connections: %d",
				dataSource.getHikariPoolMXBean().getActiveConnections(),
				dataSource.getHikariPoolMXBean().getIdleConnections(),
				dataSource.getHikariPoolMXBean().getTotalConnections());
	}
}
