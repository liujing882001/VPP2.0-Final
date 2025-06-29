package com.example.vvpdomain;

import com.example.vvpdomain.entity.RevenueParameterDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RevenueParameterRepository extends JpaRepository<RevenueParameterDto, String>, JpaSpecificationExecutor<RevenueParameterDto> {

	List<RevenueParameterDto> findAllByCategory(String category);
}
