package com.example.vvpdomain;

import com.example.vvpdomain.entity.RevenueLoadDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RevenueLoadDateRepository extends JpaRepository<RevenueLoadDto, String>, JpaSpecificationExecutor<RevenueLoadDto> {

	List<RevenueLoadDto> findAllByProjectId(String projectId);

	List<RevenueLoadDto> findAllByProjectIdAndTimeBetween(String projectId, Date st, Date et);
}
