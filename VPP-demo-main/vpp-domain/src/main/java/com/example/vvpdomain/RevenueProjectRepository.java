package com.example.vvpdomain;

import com.example.vvpdomain.entity.RevenueProjectInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RevenueProjectRepository extends JpaRepository<RevenueProjectInfo, String>, JpaSpecificationExecutor<RevenueProjectInfo> {


	@Query(value = "select c.id from RevenueProjectInfo c")
	List<String> findIDs();
}
