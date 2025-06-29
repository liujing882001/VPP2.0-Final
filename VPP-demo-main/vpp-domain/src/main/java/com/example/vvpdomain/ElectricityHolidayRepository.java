package com.example.vvpdomain;

import com.example.vvpdomain.entity.ElectricityHolidayInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface ElectricityHolidayRepository extends JpaRepository<ElectricityHolidayInfo, String>,
		JpaSpecificationExecutor<ElectricityHolidayInfo> {

	List<ElectricityHolidayInfo> findAllByPk_NodeIdAndPk_DateBetween(String nodeId, LocalDate startOfMonth, LocalDate endOfMonth);
}
