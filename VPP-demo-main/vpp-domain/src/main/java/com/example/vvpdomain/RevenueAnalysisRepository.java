package com.example.vvpdomain;

import com.example.vvpdomain.entity.RevenueAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository

public interface RevenueAnalysisRepository extends JpaRepository<RevenueAnalysis, String>, JpaSpecificationExecutor<RevenueAnalysis> {



}
